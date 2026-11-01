package com.example.sabacontinuidad.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.sabacontinuidad.data.local.AppDatabase
import com.example.sabacontinuidad.data.local.IncidenteEntity
import com.example.sabacontinuidad.data.local.aEntidad
import com.example.sabacontinuidad.data.local.aModelo
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

enum class ResultadoGuardado { ENVIADO, PENDIENTE }

class IncidenteRepository(context: Context) {
    private val contexto = context.applicationContext
    private val supabase = SupabaseClientProvider.client
    private val dao = AppDatabase.obtener(contexto).incidenteDao()

    /** Lo que ve la app: siempre sale de la base local (Room). */
    val incidentes: Flow<List<Incidente>> =
        dao.observarTodos().map { lista -> lista.map { it.aModelo() } }

    /** Reenvía los pendientes y descarga lo último de Supabase. Devuelve false si no hay internet. */
    suspend fun sincronizar(): Boolean {
        if (!hayInternet()) return false
        reenviarPendientes()
        descargarYGuardar()
        return true
    }

    suspend fun guardar(borrador: IncidenteBorrador): ResultadoGuardado {
        if (!hayInternet()) {
            dao.upsert(
                IncidenteEntity(
                    id = "local-${UUID.randomUUID()}",
                    equipo = borrador.codigoEquipo.trim(),
                    area = borrador.area,
                    problema = borrador.problema,
                    descripcion = borrador.descripcion.trim(),
                    fecha = LocalDateTime.now().toString(),
                    pendiente = true,
                    codigoEquipo = borrador.codigoEquipo.trim()
                )
            )
            return ResultadoGuardado.PENDIENTE
        }

        enviarARemoto(borrador)
        runCatching { descargarYGuardar() }
        return ResultadoGuardado.ENVIADO
    }

    suspend fun limpiarCache() {
        dao.borrarEnviados()
    }

    private suspend fun reenviarPendientes() {
        dao.pendientes().forEach { pendiente ->
            runCatching {
                enviarARemoto(
                    IncidenteBorrador(
                        codigoEquipo = pendiente.codigoEquipo,
                        area = pendiente.area,
                        problema = pendiente.problema,
                        descripcion = pendiente.descripcion
                    )
                )
            }.onSuccess { dao.borrar(pendiente.id) }
        }
    }

    private suspend fun descargarYGuardar() {
        val remotos = descargar()
        dao.reemplazarEnviados(remotos.map { it.aEntidad() })
    }

    private suspend fun descargar(): List<Incidente> {
        val parkingsAsignados = supabase.from("parkings")
            .select()
            .decodeList<ParkingFila>()
        if (parkingsAsignados.isEmpty()) {
            error("Tu usuario aún no está asignado a un parking. Pide al administrador que te agregue en parking_miembros.")
        }

        val filas = supabase.from("incidentes")
            .select()
            .decodeList<IncidenteFila>()

        if (filas.isEmpty()) return emptyList()

        val equipos = supabase.from("equipos")
            .select()
            .decodeList<EquipoFila>()
            .associateBy { it.id }

        return filas.map { fila ->
            val equipo = equipos[fila.equipoId]
            Incidente(
                id = fila.id,
                equipo = equipo?.let { "${it.codigo} · ${it.nombre}" } ?: fila.equipoId,
                area = fila.area,
                problema = fila.problema,
                descripcion = fila.descripcion,
                fecha = fila.creadoEn
            )
        }
    }

    private suspend fun enviarARemoto(incidente: IncidenteBorrador) {
        val usuarioId = supabase.auth.currentUserOrNull()?.id
            ?: error("La sesión de Supabase no está activa. Vuelve a iniciar sesión.")

        // RLS devuelve únicamente equipos de los parkings asignados al usuario.
        val equiposCoincidentes = supabase.from("equipos")
            .select()
            .decodeList<EquipoFila>()
            .filter { it.codigo.equals(incidente.codigoEquipo.trim(), ignoreCase = true) }

        val equipo = when (equiposCoincidentes.size) {
            0 -> error("No encontré ese código de equipo en tus parkings asignados.")
            1 -> equiposCoincidentes.first()
            else -> error("Ese código aparece en más de un parking. Elige un código único.")
        }

        supabase.from("incidentes").insert(
            IncidenteNuevo(
                parkingId = equipo.parkingId,
                equipoId = equipo.id,
                reportadoPor = usuarioId,
                area = incidente.area,
                problema = incidente.problema,
                descripcion = incidente.descripcion.trim()
            )
        )
    }

    private fun hayInternet(): Boolean {
        val cm = contexto.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capacidades = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}

data class IncidenteBorrador(
    val codigoEquipo: String,
    val area: String,
    val problema: String,
    val descripcion: String
)

@Serializable
private data class ParkingFila(val id: String)

@Serializable
private data class IncidenteFila(
    val id: String,
    @SerialName("equipo_id") val equipoId: String,
    val area: String,
    val problema: String,
    val descripcion: String,
    @SerialName("creado_en") val creadoEn: String
)

@Serializable
private data class EquipoFila(
    val id: String,
    @SerialName("parking_id") val parkingId: String,
    val codigo: String,
    val nombre: String
)

@Serializable
private data class IncidenteNuevo(
    @SerialName("parking_id") val parkingId: String,
    @SerialName("equipo_id") val equipoId: String,
    @SerialName("reportado_por") val reportadoPor: String,
    val area: String,
    val problema: String,
    val descripcion: String
)