package com.example.sabacontinuidad.ui.incidentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val contactOptions = listOf("Mesa de ayuda", "Proveedor de barreras")
private val channelOptions = listOf("Llamada telefónica", "Correo electrónico", "Mensaje", "Visita presencial")
private val resultOptions = listOf("Respondió", "No respondió", "Dejó mensaje", "Pendiente")
private val nextActionOptions = listOf("Verificar recuperación tras la visita", "Volver a contactar", "Escalar a supervisor", "Cerrar incidente")
private val ownerOptions = listOf("Carlos Muñoz", "María González")

@Composable
fun RegisterManagementScreen(
    modifier: Modifier = Modifier,
    incident: IncidentUi = IncidentUi.Demo,
    registeredBy: String = "Carlos Muñoz",
    registeredAt: String = "5 oct 2026 · 17:05",
    onBack: () -> Unit = {},
    onSave: (ManagementForm) -> Unit = {}
) {
    var contact by remember { mutableStateOf("Proveedor de barreras") }
    var channel by remember { mutableStateOf("Llamada telefónica") }
    var result by remember { mutableStateOf("Respondió") }
    var ticket by remember(incident.ticket) { mutableStateOf(incident.ticket) }
    var summary by remember { mutableStateOf("Visita técnica confirmada para las 17:20.") }
    var nextAction by remember { mutableStateOf("Verificar recuperación tras la visita") }
    var owner by remember { mutableStateOf("Carlos Muñoz") }

    Scaffold(modifier = modifier, containerColor = Color.White,
        bottomBar = {
            Column(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                IncAction("Guardar gestión", Icons.Outlined.Save,
                    onClick = { onSave(ManagementForm(contact, channel, result, ticket, summary, nextAction, owner)) },
                    filled = true)
                Spacer(Modifier.height(6.dp))
                Text("ⓘ  Se añadirá a la línea de tiempo.", fontSize = 11.sp, color = IncColor.Muted)
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())) {
            IncHeader("Registrar gestión", "${incident.code} · ${incident.center} · ${incident.equipmentName}", onBack)
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                IncDropdown("Contacto", contact, contactOptions, { contact = it })
                IncDropdown("Medio de contacto", channel, channelOptions, { channel = it })
                IncDropdown("Resultado del contacto", result, resultOptions, { result = it })
                IncTextField("Ticket de soporte", ticket, { ticket = it })
                IncTextField("Resumen de la gestión", summary, { summary = it }, singleLine = false, minHeight = 64.dp, maxLength = 500)
                IncDropdown("Próxima acción", nextAction, nextActionOptions, { nextAction = it })
                IncDropdown("Responsable", owner, ownerOptions, { owner = it })
                IncCard(fill = IncColor.Gray, outline = IncColor.Gray) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Outlined.Person, null, tint = IncColor.Ink)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Registrado por", fontSize = 11.sp, color = IncColor.Muted)
                            Text(registeredBy, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IncColor.Ink)
                            Text(registeredAt, fontSize = 11.sp, color = IncColor.Muted)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IncDropdown(label: String, value: String, options: List<String>, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
        Text(label, fontSize = 11.sp, color = IncColor.Muted)
        Spacer(Modifier.height(4.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = value, onValueChange = {}, readOnly = true,
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                textStyle = TextStyle(fontSize = 14.sp, color = IncColor.Ink),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IncColor.Green, unfocusedBorderColor = IncColor.Border)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(text = { Text(option, fontSize = 14.sp) }, onClick = {
                        onSelected(option); expanded = false
                    })
                }
            }
        }
    }
}

@Composable
private fun IncTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    minHeight: Dp = 40.dp,
    maxLength: Int? = null
) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, fontSize = 11.sp, color = IncColor.Muted)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { if (maxLength == null || it.length <= maxLength) onValueChange(it) },
            modifier = Modifier.fillMaxWidth().heightIn(min = minHeight),
            singleLine = singleLine,
            minLines = if (singleLine) 1 else 3,
            shape = RoundedCornerShape(8.dp),
            textStyle = TextStyle(fontSize = 14.sp, color = IncColor.Ink),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IncColor.Green, unfocusedBorderColor = IncColor.Border)
        )
        if (maxLength != null) {
            Text("${value.length}/$maxLength", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End, fontSize = 11.sp, color = IncColor.Muted)
        }
    }
}
