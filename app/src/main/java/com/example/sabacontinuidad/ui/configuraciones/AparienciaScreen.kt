package com.example.sabacontinuidad.ui.configuraciones

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AparienciaScreen(
    usuario: String,
    modo: ModoTema,
    alElegirModo: (ModoTema) -> Unit,
    alVolver: () -> Unit,
    barraInferior: @Composable () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val temaActual = if (modo.usaTemaOscuro()) "Oscuro" else "Claro"

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = alVolver) {
                    Icon(Icons.Outlined.ChevronLeft, contentDescription = "Volver a Perfil")
                }
                Column {
                    Text("Apariencia", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Perfil · $usuario", fontSize = 13.sp, color = colores.onSurfaceVariant)
                }
            }

            Text("Elige cómo se ve la aplicación.")

            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ModoTema.entries.forEach { opcion ->
                    val seleccionada = opcion == modo

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = if (seleccionada) colores.primaryContainer else colores.surface,
                        border = BorderStroke(
                            1.dp,
                            if (seleccionada) colores.primary else colores.outlineVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = seleccionada,
                                    role = Role.RadioButton,
                                    onClick = { alElegirModo(opcion) }
                                )
                                .heightIn(min = 88.dp)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MiniaturaTema(opcion, Modifier.size(66.dp))

                            Column(Modifier.weight(1f)) {
                                Text(opcion.titulo, fontWeight = FontWeight.Bold)
                                Text(
                                    opcion.descripcion,
                                    fontSize = 13.sp,
                                    lineHeight = 17.sp,
                                    color = colores.onSurfaceVariant
                                )
                            }

                            RadioButton(selected = seleccionada, onClick = null)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colores.surfaceVariant, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Info, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text("Tema actual: $temaActual", fontSize = 14.sp)
            }

            Text("Vista previa", fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(ModoTema.CLARO, ModoTema.OSCURO).forEach { opcion ->
                    Column(
                        modifier = Modifier.weight(1f)
                            .background(colores.surfaceVariant, RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Text("Modo ${opcion.titulo.lowercase()}", fontSize = 12.sp)
                        MiniaturaTema(opcion, Modifier.fillMaxWidth().height(56.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniaturaTema(modo: ModoTema, modifier: Modifier = Modifier) {
    val variantes = when (modo) {
        ModoTema.SISTEMA -> listOf(ModoTema.CLARO, ModoTema.OSCURO)
        else -> listOf(modo)
    }

    Row(modifier.clip(RoundedCornerShape(7.dp))) {
        variantes.forEach { variante ->
            val oscuro = variante == ModoTema.OSCURO
            val fondo = if (oscuro) Color(0xFF10221C) else Color.White
            val verde = if (oscuro) Color(0xFF8DECC7) else Color(0xFF0E604C)

            Column(
                modifier = Modifier.weight(1f).fillMaxHeight()
                    .background(fondo).padding(horizontal = 5.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    "saba",
                    color = verde,
                    fontSize = if (modo == ModoTema.SISTEMA) 9.sp else 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                repeat(2) {
                    Spacer(
                        Modifier.fillMaxWidth(.8f).height(3.dp)
                            .background(verde.copy(alpha = .35f))
                    )
                }
                Spacer(
                    Modifier.fillMaxWidth(.45f).height(5.dp)
                        .background(verde, RoundedCornerShape(4.dp))
                )
            }
        }
    }
}