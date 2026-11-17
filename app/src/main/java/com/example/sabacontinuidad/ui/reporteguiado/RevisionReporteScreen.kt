package com.example.sabacontinuidad.ui.reporteguiado

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sabacontinuidad.viewmodel.IncidenteViewModel

@Composable
fun RevisionReporteScreen(
    vm: IncidenteViewModel,
    alVolver: () -> Unit,
    alEditar: () -> Unit,
    alEnviado: () -> Unit,
    alLlamarSoporte: () -> Unit
) {
    val formulario by vm.form.collectAsState()
    val cargando by vm.cargando.collectAsState()

    if (formulario.avisoDuplicado) {
        AlertDialog(
            onDismissRequest = {
                if (!cargando) {
                    vm.cancelarDuplicado()
                }
            },
            title = {
                Text("Posible reporte duplicado")
            },
            text = {
                Text(
                    "Ya existe un reporte similar para este equipo. " +
                            "¿Quieres enviarlo de todas formas?"
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !cargando,
                    onClick = {
                        vm.confirmarDuplicado(alEnviado)
                    }
                ) {
                    Text("Enviar igualmente")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !cargando,
                    onClick = {
                        vm.cancelarDuplicado()
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(
                onClick = alVolver,
                enabled = !cargando
            ) {
                Text("Volver")
            }

            Text(
                text = "Revisa tu reporte",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Paso 3 de 3 · Revisión",
                style = MaterialTheme.typography.titleMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Equipo",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(formulario.equipo)

                    Text(
                        text = "Área o ubicación",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(formulario.area)

                    Text(
                        text = "Problema",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(formulario.problema)

                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(formulario.descripcion)
                }
            }

            formulario.errorServidor?.let { mensaje ->
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (!formulario.valido) {
                Text(
                    text = "Faltan datos válidos. Vuelve a editar el reporte.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (cargando) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    vm.enviar(alEnviado)
                },
                enabled = formulario.valido && !cargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (cargando) {
                        "Enviando..."
                    } else {
                        "Enviar reporte"
                    }
                )
            }

            OutlinedButton(
                onClick = alEditar,
                enabled = !cargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Editar reporte")
            }

            TextButton(
                onClick = alLlamarSoporte,
                enabled = !cargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Contactar soporte")
            }
        }
    }
}
