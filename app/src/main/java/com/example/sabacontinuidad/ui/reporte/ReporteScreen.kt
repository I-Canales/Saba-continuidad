package com.example.sabacontinuidad.ui.reporte

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.sabacontinuidad.viewmodel.IncidenteViewModel

private val AREAS = listOf("Norte", "Sur", "Acceso", "Pagos")
private val PROBLEMAS = listOf(
    "No enciende",
    "Falla de lectura",
    "Sin comunicación",
    "Daño físico",
    "Otro"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporteScreen(
    vm: IncidenteViewModel,
    alVolver: () -> Unit
) {
    val form by vm.form.collectAsState()
    val contexto = LocalContext.current

    val dictado = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == Activity.RESULT_OK) {
            val texto = resultado.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (texto != null) vm.agregarDictado(texto)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportar incidente") },
                navigationIcon = {
                    TextButton(onClick = {
                        vm.limpiar()
                        alVolver()
                    }) {
                        Text("‹ Volver", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            AnimatedVisibility(visible = form.intentoEnviar && !form.valido) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "Revisa los campos marcados.",
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            OutlinedTextField(
                value = form.equipo,
                onValueChange = vm::onEquipo,
                label = { Text("Equipo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = form.intentoEnviar && form.errorEquipo != null,
                supportingText = {
                    val error = form.errorEquipo
                    if (form.intentoEnviar && error != null) Text(error)
                }
            )

            GrupoChips(
                titulo = "Área de atención",
                opciones = AREAS,
                seleccion = form.area,
                error = if (form.intentoEnviar) form.errorArea else null,
                alSeleccionar = vm::onArea
            )

            GrupoChips(
                titulo = "Problema",
                opciones = PROBLEMAS,
                seleccion = form.problema,
                error = if (form.intentoEnviar) form.errorProblema else null,
                alSeleccionar = vm::onProblema
            )

            OutlinedTextField(
                value = form.descripcion,
                onValueChange = vm::onDescripcion,
                label = { Text("Descripción") },
                minLines = 4,
                modifier = Modifier.fillMaxWidth(),
                isError = form.intentoEnviar && form.errorDescripcion != null,
                supportingText = {
                    val error = form.errorDescripcion
                    if (form.intentoEnviar && error != null) {
                        Text(error)
                    } else {
                        Text("${form.descripcion.length}/300")
                    }
                }
            )

            OutlinedButton(
                onClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                        )
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-CL")
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Describe el incidente")
                    }
                    try {
                        dictado.launch(intent)
                    } catch (_: ActivityNotFoundException) {
                        Toast.makeText(
                            contexto,
                            "Este dispositivo no tiene dictado por voz.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Dictar con voz")
            }

            Button(
                onClick = { vm.enviar(alGuardar = alVolver) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Guardar incidente")
            }
        }
    }

    if (form.avisoDuplicado) {
        AlertDialog(
            onDismissRequest = { vm.cancelarDuplicado() },
            title = { Text("Posible duplicado") },
            text = {
                Text("Ya existe un incidente con el mismo equipo y problema. ¿Guardar de todos modos?")
            },
            confirmButton = {
                TextButton(onClick = { vm.confirmarDuplicado(alVolver) }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { vm.cancelarDuplicado() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun GrupoChips(
    titulo: String,
    opciones: List<String>,
    seleccion: String,
    error: String?,
    alSeleccionar: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = titulo, style = MaterialTheme.typography.titleSmall)

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            opciones.forEach { opcion ->
                FilterChip(
                    selected = seleccion == opcion,
                    onClick = { alSeleccionar(opcion) },
                    label = { Text(opcion) }
                )
            }
        }

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}