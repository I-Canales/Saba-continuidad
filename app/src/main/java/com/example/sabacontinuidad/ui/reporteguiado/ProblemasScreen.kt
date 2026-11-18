package com.example.sabacontinuidad.ui.reporteguiado

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
fun ProblemaScreen(
    vm: IncidenteViewModel,
    alCambiarEquipo: () -> Unit,
    alContinuar: () -> Unit,
    alVolver: () -> Unit
) {
    val formulario by vm.form.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = alVolver) {
                Text("Volver")
            }

            Text(
                text = "Describe el problema",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Paso 2 de 3 · Problema",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Equipo: ${formulario.equipo}",
                style = MaterialTheme.typography.titleMedium
            )

            TextButton(onClick = alCambiarEquipo) {
                Text("Cambiar equipo")
            }

            OutlinedTextField(
                value = formulario.area,
                onValueChange = vm::onArea,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Área o ubicación")
                },
                placeholder = {
                    Text("Ejemplo: Entrada principal")
                },
                singleLine = true
            )

            OutlinedTextField(
                value = formulario.problema,
                onValueChange = vm::onProblema,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Problema")
                },
                placeholder = {
                    Text("Ejemplo: No permite realizar pagos")
                },
                singleLine = true
            )

            OutlinedTextField(
                value = formulario.descripcion,
                onValueChange = vm::onDescripcion,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Descripción")
                },
                placeholder = {
                    Text("Explica qué ocurrió y qué observaste.")
                },
                minLines = 4,
                supportingText = {
                    Text(
                        text = "${formulario.descripcion.length}/300 caracteres. " +
                                "Mínimo 10."
                    )
                },
                isError = formulario.descripcion.isNotEmpty() &&
                        formulario.errorDescripcion != null
            )

            Button(
                onClick = alContinuar,
                enabled = formulario.valido,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Revisar reporte")
            }
        }
    }
}
