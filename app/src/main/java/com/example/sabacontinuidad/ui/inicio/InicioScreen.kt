package com.example.sabacontinuidad.ui.inicio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sabacontinuidad.data.Incidente
import com.example.sabacontinuidad.ui.componentes.BarraNavegacion

@Composable
fun InicioScreen(
    usuario: String,
    incidentes: List<Incidente>,
    errorCarga: String?,
    aviso: String?,
    alCerrarAviso: () -> Unit,
    alReportar: () -> Unit,
    alEscanear: () -> Unit = alReportar,
    alVerIncidentes: () -> Unit,
    alAbrirConfiguraciones: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    var dialogo by rememberSaveable { mutableStateOf<String?>(null) }

    val resumenIncidentes = when {
        errorCarga != null -> "No se pudieron cargar los incidentes."
        incidentes.isEmpty() -> "No hay incidentes cargados."
        incidentes.size == 1 -> "1 incidente cargado."
        else -> "${incidentes.size} incidentes cargados."
    }

    Scaffold(
        containerColor = colores.surface,
        contentColor = colores.onSurface,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            BarraInicioSaba(
                seleccionada = 0,
                alSeleccionar = { indice ->
                    when (indice) {
                        0 -> dialogo = null
                        1 -> alVerIncidentes()
                        2 -> dialogo = "Resumen"
                        3 -> alAbrirConfiguraciones()
                    }
                }
            )
        }
    ) { espacio ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacio)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inicio",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = { dialogo = "Notificaciones" }) {
                    Text("🔔", fontSize = 24.sp)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Hola, $usuario",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Buen turno. Aquí tienes la información de tu parking.",
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                    color = colores.onSurfaceVariant
                )
            }

            TarjetaInicio(onClick = { dialogo = "Seleccionar parking" }) {
                Row(
                    modifier = Modifier.padding(
                        start = 11.dp,
                        end = 14.dp,
                        top = 9.dp,
                        bottom = 9.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                color = colores.primary,
                                shape = RoundedCornerShape(7.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "P",
                            color = colores.onPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Demo Centro",
                        modifier = Modifier.weight(1f),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text("⌄", fontSize = 20.sp)
                }
            }

            AccionInicio(
                titulo = "Reportar un problema",
                icono = "⚠️",
                fondo = colores.primary,
                color = colores.onPrimary,
                borde = null,
                onClick = alReportar
            )

            AccionInicio(
                titulo = "Ver estado del parking",
                icono = "📊",
                onClick = { dialogo = "Estado del parking" }
            )

            AccionInicio(
                titulo = "Registrar mantenimiento",
                icono = "🔧",
                onClick = { dialogo = "Mantenimiento" }
            )

            TarjetaInicio(onClick = { dialogo = "Resumen" }) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 15.dp,
                        vertical = 10.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("📋", fontSize = 22.sp)

                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Resumen de incidentes",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = resumenIncidentes,
                            fontSize = 12.sp,
                            color = colores.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "Ver resumen",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colores.primary
                    )
                }
            }

            AccionInicio(
                titulo = "Escanear QR",
                icono = "📷",
                color = colores.primary,
                borde = colores.primary,
                centrado = true,
                onClick = alEscanear
            )

            AccionInicio(
                titulo = "Urgencia",
                icono = "📞",
                fondo = colores.errorContainer,
                color = colores.onErrorContainer,
                borde = colores.error,
                centrado = true,
                onClick = { dialogo = "Urgencia" }
            )

            errorCarga?.let { mensaje ->
                Text(
                    text = mensaje,
                    fontSize = 13.sp,
                    color = colores.error
                )
            }

            aviso?.let { mensaje ->
                Text(
                    text = mensaje,
                    fontSize = 13.sp,
                    color = colores.onSurfaceVariant
                )

                TextButton(onClick = alCerrarAviso) {
                    Text("Entendido")
                }
            }
        }
    }

    dialogo?.let { titulo ->
        val mensaje = when (titulo) {
            "Seleccionar parking" ->
                "Parking actual: Demo Centro."

            "Estado del parking" ->
                "Aún no hay información disponible sobre el estado del parking."

            "Notificaciones" ->
                "Aún no hay notificaciones disponibles."

            "Resumen" -> when {
                errorCarga != null -> errorCarga
                incidentes.isEmpty() -> "No hay incidentes cargados."
                else -> incidentes.joinToString("\n\n") { incidente ->
                    "${incidente.equipo}\n${incidente.problema}\n${incidente.descripcion}"
                }
            }

            "Mantenimiento" ->
                "El registro de mantenimiento aún no está disponible."

            "Urgencia" ->
                "Los contactos de soporte y emergencia aún no están configurados."

            else -> ""
        }

        AlertDialog(
            onDismissRequest = { dialogo = null },
            title = {
                Text(titulo, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(mensaje, fontSize = 15.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { dialogo = null }) {
                    Text("Cerrar")
                }
            },
            dismissButton = {
                if (titulo == "Resumen" && incidentes.isEmpty() && errorCarga == null) {
                    TextButton(
                        onClick = {
                            dialogo = null
                            alReportar()
                        }
                    ) {
                        Text("Registrar incidente")
                    }
                }
            }
        )
    }
}

// Inicio y DetalleIncidente pueden llamar a esta función.
// El diseño de la barra está en ui/componentes/BarraNavegacion.kt.
@Composable
fun BarraInicioSaba(
    seleccionada: Int,
    alSeleccionar: (Int) -> Unit
) {
    BarraNavegacion(
        seleccionada = seleccionada,
        alSeleccionar = alSeleccionar
    )
}

@Composable
private fun TarjetaInicio(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val colores = MaterialTheme.colorScheme

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colores.surface,
        contentColor = colores.onSurface,
        border = BorderStroke(1.dp, colores.outlineVariant),
        content = content
    )
}

@Composable
private fun AccionInicio(
    titulo: String,
    icono: String,
    fondo: Color = MaterialTheme.colorScheme.surface,
    color: Color = MaterialTheme.colorScheme.onSurface,
    borde: Color? = MaterialTheme.colorScheme.outlineVariant,
    centrado: Boolean = false,
    onClick: () -> Unit
) {
    val anchoBorde = if (centrado) 1.5.dp else 1.dp

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = fondo,
        contentColor = color,
        border = borde?.let { BorderStroke(anchoBorde, it) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (centrado) {
                Arrangement.Center
            } else {
                Arrangement.Start
            }
        ) {
            Text(icono, fontSize = 20.sp)
            Spacer(Modifier.width(12.dp))

            Text(
                text = titulo,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (!centrado) {
                Spacer(Modifier.weight(1f))

                Text(
                    text = "›",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}