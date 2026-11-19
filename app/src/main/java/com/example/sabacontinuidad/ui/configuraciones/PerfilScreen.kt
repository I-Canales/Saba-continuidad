package com.example.sabacontinuidad.ui.configuraciones

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PerfilScreen(
    usuario: String,
    modo: ModoTema,
    alAbrirApariencia: () -> Unit,
    alAbrirAyuda: () -> Unit,
    alCerrarSesion: () -> Unit,
    barraInferior: @Composable () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val temaActual = if (modo.usaTemaOscuro()) "Oscuro" else "Claro"
    val iniciales = usuario.substringBefore("@")
        .split(" ", ".", "_")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.take(1).uppercase() }

    Scaffold(
        containerColor = colores.surface,
        bottomBar = barraInferior
    ) { espacio ->
        Column(
            modifier = Modifier
                .padding(espacio)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column {
                Text("Perfil", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Tu cuenta y la aplicación", color = colores.onSurfaceVariant)
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = colores.surfaceVariant,
                border = BorderStroke(1.dp, colores.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(60.dp),
                        shape = CircleShape,
                        color = colores.primaryContainer,
                        border = BorderStroke(1.dp, colores.primary)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                iniciales,
                                color = colores.primary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column {
                        Text(usuario, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Demo Centro", color = colores.onSurfaceVariant)
                    }
                }
            }

            Text("Configuración", fontWeight = FontWeight.Bold)

            OpcionPerfil(
                titulo = "Apariencia",
                icono = Icons.Outlined.Contrast,
                detalle = "Tema actual: $temaActual",
                alPresionar = alAbrirApariencia
            )

            OpcionPerfil(
                titulo = "Ayuda de acceso",
                icono = Icons.Outlined.HelpOutline,
                alPresionar = alAbrirAyuda
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = alCerrarSesion,
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colores.errorContainer,
                    contentColor = colores.onErrorContainer
                )
            ) {
                Icon(Icons.Outlined.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cerrar sesión")
            }
        }
    }
}

@Composable
private fun OpcionPerfil(
    titulo: String,
    icono: ImageVector,
    detalle: String? = null,
    alPresionar: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val destacada = detalle != null

    Surface(
        onClick = alPresionar,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (destacada) colores.primaryContainer else colores.surface,
        border = BorderStroke(
            1.dp,
            if (destacada) colores.primary else colores.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.heightIn(min = 70.dp).padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(
                    if (destacada) colores.surface else colores.surfaceVariant,
                    RoundedCornerShape(8.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = colores.primary)
            }

            Column(Modifier.weight(1f)) {
                Text(titulo, fontWeight = FontWeight.Bold)
                if (detalle != null) {
                    Text(detalle, fontSize = 13.sp, color = colores.onSurfaceVariant)
                }
            }

            Icon(Icons.Outlined.ChevronRight, contentDescription = null)
        }
    }
}