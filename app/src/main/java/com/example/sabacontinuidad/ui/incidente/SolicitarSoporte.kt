package com.example.sabacontinuidad.ui.incidentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SupportRequestScreen(
    modifier: Modifier = Modifier,
    incident: IncidentUi = IncidentUi.Demo,
    contacts: List<SupportContact> = listOf(
        SupportContact("Mesa de ayuda", "Soporte operativo", "Contacto corporativo", Icons.Outlined.Headset, ""),
        SupportContact("Proveedor de barreras", "Equipo ${incident.equipmentCode}", "Contacto corporativo", Icons.Outlined.Build, "")
    ),
    onBack: () -> Unit = {},
    onOpenIncident: () -> Unit = {},
    onCall: (SupportContact) -> Unit = {},
    onRegisterManagement: () -> Unit = {}
) {
    Scaffold(modifier = modifier, containerColor = Color.White,
        bottomBar = {
            Column(Modifier.background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp)) {
                IncAction("Registrar gestión", Icons.Outlined.Description, onRegisterManagement, filled = true)
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())) {
            IncHeader("Solicitar soporte", "${incident.code} · ${incident.center}", onBack)
            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                IncCard(fill = IncColor.BlueSoft, outline = IncColor.BlueSoft, onClick = onOpenIncident) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Description, null, tint = IncColor.Ink, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Incidente ${incident.code}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IncColor.Ink)
                            Text("${incident.equipmentName} · Equipo ${incident.equipmentCode}", fontSize = 11.sp, color = IncColor.Muted)
                        }
                        Icon(Icons.Outlined.ChevronRight, null, tint = IncColor.Ink)
                    }
                }
                Text("Contactos de soporte", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IncColor.Ink)
                contacts.forEach { contact ->
                    IncCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(contact.icon, null, tint = IncColor.Ink, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(contact.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IncColor.Ink)
                                Text(contact.detail, fontSize = 11.sp, color = IncColor.Muted)
                                Text(contact.kind, fontSize = 11.sp, color = IncColor.Muted)
                            }
                            OutlinedButton(onClick = { onCall(contact) }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                shape = RoundedCornerShape(8.dp), border = androidx.compose.foundation.BorderStroke(1.dp, IncColor.Green)) {
                                Icon(Icons.Outlined.Phone, null, tint = IncColor.Green, modifier = Modifier.size(15.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Llamar", fontSize = 12.sp, color = IncColor.Green)
                            }
                        }
                    }
                }
                IncCard(fill = IncColor.Gray, outline = IncColor.Gray) {
                    Text("Ticket asociado", fontSize = 11.sp, color = IncColor.Muted)
                    Text(incident.ticket, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IncColor.Ink)
                }
                IncCard {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Outlined.Info, null, tint = IncColor.Ink, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Después del contacto, registra el resultado.", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IncColor.Green)
                            Text("El caso conserva su historial y responsable.", fontSize = 11.sp, color = IncColor.Muted)
                        }
                    }
                }
                IncCard(fill = IncColor.GreenSoft, outline = IncColor.GreenBorder) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Outlined.CloudDone, null, tint = IncColor.Green, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Contactos disponibles sin conexión", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IncColor.Ink)
                            Text("La información de contacto se sincroniza y se guarda para uso sin conexión.", fontSize = 11.sp, color = IncColor.Ink)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
