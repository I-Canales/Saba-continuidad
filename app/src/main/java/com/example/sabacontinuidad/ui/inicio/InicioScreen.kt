package com.example.sabacontinuidad.ui.inicio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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

@Composable
fun InicioScreen(
    usuario: String,
    incidentes: List<Incidente>,
    errorCarga: String?,
    aviso: String?,
    alCerrarAviso: () -> Unit,
    alReportar: () -> Unit,
    alCerrarSesion: () -> Unit,
    alEscanear: () -> Unit = alReportar,
    alVerIncidentes: () -> Unit,
    alAbrirConfiguraciones: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    var dialogo by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = colores.surface,
        contentColor = colores.onSurface,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            Column {
                HorizontalDivider(color = colores.outlineVariant)

                NavigationBar(
                    containerColor = colores.surface,
                    tonalElevation = 0.dp
                ) {
                    val pestanas = listOf(
                        "Inicio" to "🏠",
                        "Incidentes" to "⚠️",
                        "Resumen" to "📊",
                        "Configuraciones" to "⚙️"
                    )

                    pestanas.forEachIndexed { indice, (nombre, icono) ->
                        NavigationBarItem(
                            selected = indice == 0,
                            onClick = {
                                when (indice) {
                                    0 -> dialogo = null
                                    1 -> alVerIncidentes()
                                    2 -> dialogo = "Resumen"
                                    3 -> alAbrirConfiguraciones()
                                }
                            },
                            icon = {
                                Text(icono, fontSize = 20.sp)
                            },
                            label = {
                                Text(nombre, fontSize = 11.sp)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedTextColor = colores.primary,
                                unselectedTextColor = colores.onSurfaceVariant,
                                indicatorColor = colores.primaryContainer
                            )
                        )
                    }
                }
            }
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
                    Box {
                        Text("🔔", fontSize = 24.sp)

                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .align(Alignment.TopEnd)
                                .background(Color(0xFFFF7A1A), CircleShape)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Hola, $usuario",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Buen turno. Aquí tienes la información más relevante del parking.",
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                    color = colores.onSurfaceVariant
                )
            }

            Tarjeta(onClick = { dialogo = "Seleccionar parking" }) {
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
                            .background(colores.primary, RoundedCornerShape(7.dp)),
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

            // Datos de demostración del prototipo.
            Tarjeta(
                onClick = { dialogo = "Estado del parking" },
                fondo = colores.tertiaryContainer,
                borde = colores.tertiary,
                colorContenido = colores.onTertiaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(15.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚠️", fontSize = 24.sp)
                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = "Salidas · Servicio degradado",
                            modifier = Modifier.weight(1f),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Flecha(colores.onTertiaryContainer)
                    }

                    Column(
                        modifier = Modifier.padding(start = 38.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Salida 2 detenida. Salida 1 disponible.", fontSize = 14.sp)
                        Dato("🕒", "Desde 15:50 · hace 1 h 10 min")
                        Dato("🔄", "Actualizado 16:40 · hace 20 min")
                    }
                }
            }

            Accion(
                titulo = "Reportar un problema",
                icono = "⚠️",
                fondo = colores.primary,
                color = colores.onPrimary,
                borde = null,
                onClick = alReportar
            )

            Accion(
                titulo = "Ver estado del parking",
                icono = "📊",
                onClick = { dialogo = "Estado del parking" }
            )

            Accion(
                titulo = "Registrar mantenimiento",
                icono = "🔧",
                onClick = { dialogo = "Mantenimiento" }
            )

            Tarjeta(onClick = { dialogo = "Pendientes del turno" }) {
                Row(
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📋", fontSize = 22.sp)
                    Spacer(Modifier.width(12.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Pendientes del turno",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "2 abiertos, incluido 1 por verificar",
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

                    Flecha(colores.primary)
                }
            }

            Accion(
                titulo = "Escanear QR",
                icono = "📷",
                color = colores.primary,
                borde = colores.primary,
                centrado = true,
                onClick = alEscanear
            )

            Accion(
                titulo = "Urgencia",
                icono = "📞",
                fondo = colores.errorContainer,
                color = colores.onErrorContainer,
                borde = colores.error,
                centrado = true,
                onClick = { dialogo = "Urgencia" }
            )

            errorCarga?.let { mensaje ->
                Text(mensaje, fontSize = 13.sp, color = colores.error)
            }

            aviso?.let { mensaje ->
                Text(mensaje, fontSize = 13.sp, color = colores.onSurfaceVariant)

                TextButton(onClick = alCerrarAviso) {
                    Text("Entendido")
                }
            }
        }
    }

    dialogo?.let { titulo ->
        val mensaje = when (titulo) {
            "Seleccionar parking" ->
                "Demo Centro es el parking de esta demostración."

            "Estado del parking", "Notificaciones" ->
                "Ejemplo del prototipo: salida 2 detenida y salida 1 disponible."

            "Pendientes del turno" ->
                "Ejemplo del prototipo: 2 incidentes abiertos, 1 por verificar."

            "Resumen" -> {
                if (incidentes.isEmpty()) {
                    "No hay incidentes cargados."
                } else {
                    incidentes.joinToString("\n\n") { incidente ->
                        "${incidente.equipo}\n${incidente.problema}\n${incidente.descripcion}"
                    }
                }
            }

            "Mantenimiento" ->
                "El registro de mantenimiento aún está pendiente de implementar."

            "Urgencia" ->
                "Los contactos de soporte y emergencia aún no están configurados."

            "Perfil" -> usuario
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
                if (titulo == "Perfil") {
                    TextButton(
                        onClick = {
                            dialogo = null
                            alCerrarSesion()
                        }
                    ) {
                        Text("Cerrar sesión", color = colores.error)
                    }
                }
            }
        )
    }
}

@Composable
private fun Tarjeta(
    onClick: () -> Unit,
    fondo: Color = MaterialTheme.colorScheme.surface,
    borde: Color = MaterialTheme.colorScheme.outlineVariant,
    colorContenido: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = fondo,
        contentColor = colorContenido,
        border = BorderStroke(1.dp, borde),
        content = content
    )
}

@Composable
private fun Accion(
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
                .heightIn(min = 50.dp)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (centrado) Arrangement.Center else Arrangement.Start
        ) {
            Text(icono, fontSize = 20.sp)
            Spacer(Modifier.width(12.dp))

            Text(
                text = titulo,
                color = color,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (!centrado) {
                Spacer(Modifier.weight(1f))
                Flecha(color)
            }
        }
    }
}

@Composable
private fun Flecha(color: Color) {
    Text(
        text = "›",
        color = color,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun Dato(icono: String, texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(icono, fontSize = 13.sp)
        Text(texto, fontSize = 13.sp)
    }
}