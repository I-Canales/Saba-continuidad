package com.example.sabacontinuidad.ui.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BarraNavegacion(
    seleccionada: Int,
    alSeleccionar: (Int) -> Unit
) {
    val colores = MaterialTheme.colorScheme

    val pestanas = listOf(
        "Inicio" to "🏠",
        "Incidentes" to "⚠️",
        "Resumen" to "📊",
        "Configuraciones" to "⚙️"
    )

    Column {
        HorizontalDivider(color = colores.outlineVariant)

        NavigationBar(
            containerColor = colores.surface,
            tonalElevation = 0.dp
        ) {
            pestanas.forEachIndexed { indice, (nombre, icono) ->
                NavigationBarItem(
                    selected = indice == seleccionada,
                    onClick = { alSeleccionar(indice) },
                    icon = { Text(icono, fontSize = 20.sp) },
                    label = { Text(nombre, fontSize = 11.sp) },
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