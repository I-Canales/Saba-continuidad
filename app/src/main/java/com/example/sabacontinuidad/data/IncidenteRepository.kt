package com.example.sabacontinuidad.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class IncidenteRepository {
    private val supabase = SupabaseClientProvider.client

    suspend fun obtenerIncidentes(): List<Incidente> {
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

    suspend fun guardar(incidente: IncidenteBorrador) {
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
