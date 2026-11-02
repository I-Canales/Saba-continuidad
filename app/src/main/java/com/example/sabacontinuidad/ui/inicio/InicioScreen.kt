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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sabacontinuidad.data.Incidente
import com.example.sabacontinuidad.ui.theme.BordeSaba
import com.example.sabacontinuidad.ui.theme.TextoSaba
import com.example.sabacontinuidad.ui.theme.VerdeSaba

private val Gris = Color(0xFF5E6B65)
private val ColorNaranja = Color(0xFFB94A0A)
private val ColorRojo = Color(0xFFD32F2F)

@Composable
fun InicioScreen(
    usuario: String,
    incidentes: List<Incidente>,
    errorCarga: String?,
    aviso: String?,
    alCerrarAviso: () -> Unit,
    alReportar: () -> Unit,
    alCerrarSesion: () -> Unit,
    alEscanear: () -> Unit = alReportar
) {
    var dialogo by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Color.White,
        contentColor = TextoSaba,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            Column {
                HorizontalDivider(color = BordeSaba)
                NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                    val tabs = listOf(
                        "Inicio" to "🏠",
                        "Incidentes" to "⚠️",
                        "Manuales" to "📖",
                        "Perfil" to "👤"
                    )
                    tabs.forEachIndexed { i, (nombre, glifo) ->
                        NavigationBarItem(
                            selected = i == 0,
                            onClick = { dialogo = if (i == 0) null else nombre },
                            icon = { Text(glifo, fontSize = 20.sp) },
                            label = { Text(nombre, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedTextColor = VerdeSaba,
                                unselectedTextColor = Gris,
                                indicatorColor = Color(0xFFE3F1EA)
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
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Inicio", fontSize = 27.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = { dialogo = "Notificaciones" }) {
                    Box {
                        Text("🔔", fontSize = 24.sp)
                        Box(
                            Modifier
                                .size(9.dp)
                                .align(Alignment.TopEnd)
                                .background(Color(0xFFFF7A1A), CircleShape)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Hola, Admin", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Buen turno. Aquí tienes la información más relevante del parking.",
                    fontSize = 15.sp, lineHeight = 21.sp, color = Gris
                )
            }

            Tarjeta(onClick = { dialogo = "Seleccionar parking" }) {
                Row(
                    Modifier.padding(start = 11.dp, end = 14.dp, top = 9.dp, bottom = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        Modifier.size(34.dp).background(VerdeSaba, RoundedCornerShape(7.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("P", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("Demo Centro", Modifier.weight(1f), fontSize = 17.sp, fontWeight = FontWeight.Medium)
                    Text("⌄", fontSize = 20.sp)
                }
            }

            Tarjeta(
                onClick = { dialogo = "Estado del parking" },
                fondo = Color(0xFFFDEBC8),
                borde = Color(0xFFF3B95F)
            ) {
                Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚠️", fontSize = 24.sp)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            buildAnnotatedString {
                                append("Salidas · ")
                                withStyle(SpanStyle(color = ColorNaranja)) { append("Servicio degradado") }
                            },
                            Modifier.weight(1f), fontSize = 17.sp, fontWeight = FontWeight.Bold
                        )
                        Flecha(TextoSaba)
                    }
                    Column(Modifier.padding(start = 38.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Salida 2 detenida. Salida 1 disponible.", fontSize = 14.sp)
                        Dato("🕒", "Desde 15:50 · hace 1 h 10 min")
                        Dato("🔄", "Actualizado 16:40 · hace 20 min")
                    }
                }
            }

            Accion("Reportar un problema", "⚠️", fondo = VerdeSaba, color = Color.White, onClick = alReportar)
            Accion("Ver estado del parking", "📊") { dialogo = "Estado del parking" }
            Accion("Registrar mantenimiento", "🔧") { dialogo = "Mantenimiento" }

            Tarjeta(onClick = { dialogo = "Pendientes del turno" }) {
                Row(
                    Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📋", fontSize = 22.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Pendientes del turno", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text("2 abiertos, incluido 1 por verificar", fontSize = 12.sp, color = Gris)
                    }
                    Text("Ver resumen", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = VerdeSaba)
                    Flecha(VerdeSaba)
                }
            }

            Accion("Escanear QR", "📷", color = VerdeSaba, borde = VerdeSaba, centrado = true, onClick = alEscanear)
            Accion(
                "Urgencia", "📞",
                fondo = Color(0xFFFDECEC), color = ColorRojo, borde = Color(0xFFEF5350), centrado = true
            ) { dialogo = "Urgencia" }

            errorCarga?.let { Text(it, fontSize = 13.sp, color = ColorRojo) }
            aviso?.let {
                Text(it, fontSize = 13.sp, color = Gris)
                TextButton(onClick = alCerrarAviso) { Text("Entendido", color = VerdeSaba) }
            }
        }
    }

    dialogo?.let { titulo ->
        val mensaje = when (titulo) {
            "Seleccionar parking" -> "Demo Centro es el parking de esta demostración."
            "Estado del parking", "Notificaciones" ->
                "Ejemplo del prototipo: salida 2 detenida y salida 1 disponible."
            "Pendientes del turno" -> "Ejemplo del prototipo: 2 incidentes abiertos, 1 por verificar."
            "Incidentes" ->
                if (incidentes.isEmpty()) "No hay incidentes cargados."
                else incidentes.joinToString("\n\n") { "${it.equipo}\n${it.problema}\n${it.descripcion}" }
            "Mantenimiento" -> "El registro de mantenimiento aún está pendiente de implementar."
            "Manuales" -> "Los manuales del parking aún están pendientes de incorporar."
            "Urgencia" -> "Los contactos de soporte y emergencia aún no están configurados."
            "Perfil" -> usuario
            else -> ""
        }
        AlertDialog(
            onDismissRequest = { dialogo = null },
            containerColor = Color.White,
            title = { Text(titulo, fontWeight = FontWeight.Bold) },
            text = {
                Column(Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState())) {
                    Text(mensaje, fontSize = 15.sp)
                }
            },
            confirmButton = { TextButton(onClick = { dialogo = null }) { Text("Cerrar", color = VerdeSaba) } },
            dismissButton = {
                if (titulo == "Perfil") {
                    TextButton(onClick = { dialogo = null; alCerrarSesion() }) {
                        Text("Cerrar sesión", color = ColorRojo)
                    }
                }
            }
        )
    }
}

@Composable
private fun Flecha(color: Color) {
    Text("›", color = color, fontSize = 24.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun Tarjeta(
    onClick: () -> Unit,
    fondo: Color = Color.White,
    borde: Color = BordeSaba,
    content: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = fondo,
        border = BorderStroke(1.dp, borde),
        content = content
    )
}

@Composable
private fun Dato(glifo: String, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(glifo, fontSize = 13.sp)
        Text(texto, fontSize = 13.sp)
    }
}

@Composable
private fun Accion(
    titulo: String,
    glifo: String,
    fondo: Color = Color.White,
    color: Color = TextoSaba,
    borde: Color? = BordeSaba,
    centrado: Boolean = false,
    onClick: () -> Unit
) {
    val ancho: Dp = if (centrado) 1.5.dp else 1.dp
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = fondo,
        border = if (fondo == VerdeSaba || borde == null) null else BorderStroke(ancho, borde)
    ) {
        Row(
            Modifier.heightIn(min = 50.dp).padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (centrado) Arrangement.Center else Arrangement.Start
        ) {
            Text(glifo, fontSize = 20.sp)
            Spacer(Modifier.width(12.dp))
            Text(titulo, color = color, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            if (!centrado) {
                Spacer(Modifier.weight(1f))
                Flecha(color)
            }
        }
    }
}