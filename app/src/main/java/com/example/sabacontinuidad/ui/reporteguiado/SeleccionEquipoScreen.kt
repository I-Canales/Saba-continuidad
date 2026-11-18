package com.example.sabacontinuidad.ui.reporteguiado

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SeleccionEquipoScreen(
    codigoActual: String,
    alElegirEquipo: (String) -> Unit,
    alContinuar: () -> Unit,
    alVolver: () -> Unit
) {
    val codigoValido = codigoActual.trim().length >= 3

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
                text = "Reportar un problema",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Paso 1 de 3 · Selección de equipo",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Ingresa el código que aparece en la etiqueta del equipo."
            )

            OutlinedTextField(
                value = codigoActual,
                onValueChange = alElegirEquipo,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Código del equipo")
                },
                placeholder = {
                    Text("Ejemplo: POS-001")
                },
                supportingText = {
                    Text("El código debe tener al menos 3 caracteres.")
                },
                singleLine = true,
                isError = codigoActual.isNotBlank() && !codigoValido
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = alContinuar,
                enabled = codigoValido,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar")
            }
        }
    }
}
