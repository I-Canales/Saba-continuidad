package com.example.sabacontinuidad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Headset
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sabacontinuidad.ui.components.ActionStyle
import com.example.sabacontinuidad.ui.components.SabaActionButton
import com.example.sabacontinuidad.ui.components.SabaBottomBar
import com.example.sabacontinuidad.ui.components.SabaCard
import com.example.sabacontinuidad.ui.components.SabaChip
import com.example.sabacontinuidad.ui.components.SabaDropdownField
import com.example.sabacontinuidad.ui.components.SabaSmallOutlinedButton
import com.example.sabacontinuidad.ui.components.SabaTextField
import com.example.sabacontinuidad.ui.components.SabaTopBar
import com.example.sabacontinuidad.ui.theme.SabaColors
import com.example.sabacontinuidad.ui.theme.SabaTheme
import com.example.sabacontinuidad.ui.theme.SabaType



enum class TimelineType { Report, Call, Visit }

data class TimelineEntry(
    val time: String,
    val author: String,
    val description: String,
    val type: TimelineType,
)

data class IncidentUi(
    val code: String,
    val center: String,
    val equipmentName: String,
    val equipmentCode: String,
    val date: String,
    val status: String,
    val impact: String,
    val summary: String,
    val owner: String,
    val nextFollowUp: String,
    val ticket: String,
    val timeline: List<TimelineEntry>,
) {
    companion object {
        val Demo = IncidentUi(
            code = "INC-024",
            center = "Demo Centro",
            equipmentName = "Barrera de salida 2",
            equipmentCode = "SAL-02",
            date = "5 oct 2026",
            status = "En atención",
            impact = "Servicio degradado",
            summary = "Salida 2 no abre. Salida 1 disponible.",
            owner = "Carlos Muñoz · Jefe de parking",
            nextFollowUp = "17:15",
            ticket = "SAU-082",
            timeline = listOf(
                TimelineEntry("15:50", "María González", "Problema reportado", TimelineType.Report),
                TimelineEntry("16:10", "Carlos Muñoz", "Soporte contactado", TimelineType.Call),
                TimelineEntry("16:40", "Soporte", "Visita en coordinación", TimelineType.Visit),
            )
        )
    }
}

data class SupportContact(
    val name: String,
    val detail: String,
    val kind: String,
    val icon: ImageVector,
    val phone: String,
)

data class ManagementForm(
    val contact: String,
    val channel: String,
    val result: String,
    val ticket: String,
    val summary: String,
    val nextAction: String,
    val owner: String,
)



@Composable
fun IncidentDetailScreen(
    modifier: Modifier = Modifier,
    incident: IncidentUi = IncidentUi.Demo,
    onBack: () -> Unit = {},
    onMenu: () -> Unit = {},
    onConfirmVisit: () -> Unit = {},
    onRegisterAction: () -> Unit = {},
    onRequestSupport: () -> Unit = {},
    onScanQr: () -> Unit = {},
    onUrgency: () -> Unit = {},
    onNavSelect: (Int) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        bottomBar = { SabaBottomBar(selected = 0, onSelect = onNavSelect) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SabaTopBar(
                title = "Incidente ${incident.code}",
                subtitle = "${incident.center} · ${incident.equipmentName}",
                onBack = onBack,
                onMenu = onMenu
            )

            Column(
                Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.CalendarToday, contentDescription = null,
                        tint = SabaColors.Ink, modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(incident.date, style = SabaType.Body)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SabaChip(
                        text = incident.status,
                        icon = Icons.Outlined.AccessTime,
                        container = SabaColors.BlueSoft,
                        content = SabaColors.BlueText
                    )
                    SabaChip(
                        text = incident.impact,
                        icon = Icons.Outlined.Warning,
                        container = SabaColors.AmberSoft,
                        content = SabaColors.AmberText
                    )
                }

                Text(incident.summary, style = SabaType.Body)

                SabaCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Person, contentDescription = null,
                            tint = SabaColors.Ink, modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Responsable del seguimiento", style = SabaType.Caption)
                            Text(incident.owner, style = SabaType.CardTitle)
                        }
                    }
                }

                SabaCard(
                    container = SabaColors.GreenSoft,
                    stroke = SabaColors.GreenBorder,
                    onClick = onConfirmVisit
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.CalendarMonth, contentDescription = null,
                            tint = SabaColors.Green, modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Confirmar visita del proveedor",
                                style = SabaType.CardTitle.copy(color = SabaColors.Green)
                            )
                            Text("Próximo seguimiento: ${incident.nextFollowUp}", style = SabaType.Caption)
                        }
                        Icon(
                            Icons.Outlined.ChevronRight, contentDescription = null,
                            tint = SabaColors.Green, modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(Modifier.height(2.dp))
                Text("Línea de tiempo", style = SabaType.Section)

                SabaCard {
                    incident.timeline.forEachIndexed { index, entry ->
                        TimelineRow(entry, isLast = index == incident.timeline.lastIndex)
                    }
                }

                SabaActionButton(
                    text = "Registrar actuación",
                    icon = Icons.Outlined.Edit,
                    onClick = onRegisterAction
                )
                SabaActionButton(
                    text = "Solicitar soporte",
                    icon = Icons.Outlined.Headset,
                    onClick = onRequestSupport,
                    style = ActionStyle.Filled
                )
                SabaActionButton(
                    text = "Escanear QR",
                    icon = Icons.Outlined.QrCodeScanner,
                    onClick = onScanQr,
                    chevron = false
                )
                SabaActionButton(
                    text = "Urgencia",
                    icon = Icons.Outlined.Phone,
                    onClick = onUrgency,
                    style = ActionStyle.Danger
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun TimelineRow(entry: TimelineEntry, isLast: Boolean) {
    val trailingIcon = when (entry.type) {
        TimelineType.Report -> Icons.Outlined.Description
        TimelineType.Call -> Icons.Outlined.Phone
        TimelineType.Visit -> Icons.Outlined.Build
    }
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {

        Box(
            Modifier
                .width(20.dp)
                .fillMaxHeight()
                .drawBehind {
                    if (!isLast) {
                        drawLine(
                            color = SabaColors.Green,
                            start = Offset(size.width / 2, 18.dp.toPx()),
                            end = Offset(size.width / 2, size.height),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }
        ) {
            Box(
                Modifier
                    .size(20.dp)
                    .align(Alignment.TopCenter)
                    .clip(CircleShape)
                    .background(SabaColors.Green),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        Column(
            Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 14.dp)
        ) {
            Text("${entry.time} · ${entry.author}", style = SabaType.CardTitle.copy(fontWeight = FontWeight.SemiBold))
            Text(entry.description, style = SabaType.Caption)
        }
        Icon(
            trailingIcon, contentDescription = null,
            tint = SabaColors.Ink, modifier = Modifier.size(16.dp)
        )
    }
}



@Composable
fun SupportRequestScreen(
    modifier: Modifier = Modifier,
    incident: IncidentUi = IncidentUi.Demo,
    contacts: List<SupportContact> = listOf(
        SupportContact("Mesa de ayuda", "Soporte operativo", "Contacto corporativo", Icons.Outlined.Headset, ""),
        SupportContact("Proveedor de barreras", "Equipo ${IncidentUi.Demo.equipmentCode}", "Contacto corporativo", Icons.Outlined.Build, ""),
    ),
    onBack: () -> Unit = {},
    onOpenIncident: () -> Unit = {},
    onCall: (SupportContact) -> Unit = {},
    onRegisterManagement: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        bottomBar = {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                SabaActionButton(
                    text = "Registrar gestión",
                    icon = Icons.Outlined.Description,
                    onClick = onRegisterManagement,
                    style = ActionStyle.Filled
                )
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SabaTopBar(
                title = "Solicitar soporte",
                subtitle = "${incident.code} · ${incident.center}",
                onBack = onBack
            )

            Column(
                Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SabaCard(
                    container = SabaColors.BlueSoft,
                    stroke = SabaColors.BlueSoft,
                    onClick = onOpenIncident
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Description, contentDescription = null,
                            tint = SabaColors.Ink, modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Incidente ${incident.code}", style = SabaType.CardTitle)
                            Text(
                                "${incident.equipmentName} · Equipo ${incident.equipmentCode}",
                                style = SabaType.Caption
                            )
                        }
                        Icon(
                            Icons.Outlined.ChevronRight, contentDescription = null,
                            tint = SabaColors.Ink, modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(Modifier.height(2.dp))
                Text("Contactos de soporte", style = SabaType.Section)

                contacts.forEach { contact ->
                    SabaCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                contact.icon, contentDescription = null,
                                tint = SabaColors.Ink, modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(contact.name, style = SabaType.CardTitle)
                                Text(contact.detail, style = SabaType.Caption)
                                Text(contact.kind, style = SabaType.Caption)
                            }
                            SabaSmallOutlinedButton(
                                text = "Llamar",
                                icon = Icons.Outlined.Phone,
                                onClick = { onCall(contact) }
                            )
                        }
                    }
                }

                SabaCard(container = SabaColors.Gray, stroke = SabaColors.Gray) {
                    Text("Ticket asociado", style = SabaType.Caption)
                    Text(incident.ticket, style = SabaType.CardTitle)
                }

                SabaCard {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Outlined.Info, contentDescription = null,
                            tint = SabaColors.Ink, modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                buildAnnotatedString {
                                    append("Después del contacto, ")
                                    withStyle(SpanStyle(color = SabaColors.Green, fontWeight = FontWeight.Bold)) {
                                        append("registra")
                                    }
                                    append(" el resultado.")
                                },
                                style = SabaType.CardTitle.copy(fontSize = 12.sp)
                            )
                            Text("El caso conserva su historial y responsable.", style = SabaType.Caption)
                        }
                    }
                }

                SabaCard(container = SabaColors.GreenSoft, stroke = SabaColors.GreenBorder) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Outlined.CloudDone, contentDescription = null,
                            tint = SabaColors.Green, modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Contactos disponibles sin conexión", style = SabaType.CardTitle.copy(fontSize = 12.sp))
                            Text(
                                "La información de contacto se sincroniza y se guarda para uso sin conexión.",
                                style = SabaType.Caption.copy(color = SabaColors.Ink)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}



private val contactOptions = listOf("Mesa de ayuda", "Proveedor de barreras")
private val channelOptions = listOf("Llamada telefónica", "Correo electrónico", "Mensaje", "Visita presencial")
private val resultOptions = listOf("Respondió", "No respondió", "Dejó mensaje", "Pendiente")
private val nextActionOptions = listOf(
    "Verificar recuperación tras la visita",
    "Volver a contactar",
    "Escalar a supervisor",
    "Cerrar incidente",
)
private val ownerOptions = listOf("Carlos Muñoz", "María González")

@Composable
fun RegisterManagementScreen(
    modifier: Modifier = Modifier,
    incident: IncidentUi = IncidentUi.Demo,
    registeredBy: String = "Carlos Muñoz",
    registeredAt: String = "5 oct 2026 · 17:05",
    onBack: () -> Unit = {},
    onSave: (ManagementForm) -> Unit = {},
) {
    var contact by remember { mutableStateOf("Proveedor de barreras") }
    var channel by remember { mutableStateOf("Llamada telefónica") }
    var result by remember { mutableStateOf("Respondió") }
    var ticket by remember { mutableStateOf(incident.ticket) }
    var summary by remember { mutableStateOf("Visita técnica confirmada para las 17:20.") }
    var nextAction by remember { mutableStateOf("Verificar recuperación tras la visita") }
    var owner by remember { mutableStateOf("Carlos Muñoz") }

    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        bottomBar = {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SabaActionButton(
                    text = "Guardar gestión",
                    icon = Icons.Outlined.Save,
                    onClick = {
                        onSave(ManagementForm(contact, channel, result, ticket, summary, nextAction, owner))
                    },
                    style = ActionStyle.Filled
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Info, contentDescription = null,
                        tint = SabaColors.Muted, modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Se añadirá a la línea de tiempo.", style = SabaType.Caption)
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SabaTopBar(
                title = "Registrar gestión",
                subtitle = "${incident.code} · ${incident.center} · ${incident.equipmentName}",
                onBack = onBack
            )

            Column(
                Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SabaDropdownField("Contacto", contact, contactOptions, { contact = it })
                SabaDropdownField("Medio de contacto", channel, channelOptions, { channel = it })
                SabaDropdownField("Resultado del contacto", result, resultOptions, { result = it })
                SabaTextField("Ticket de soporte", ticket, { ticket = it })
                SabaTextField(
                    label = "Resumen de la gestión",
                    value = summary,
                    onValueChange = { summary = it },
                    singleLine = false,
                    minHeight = 64.dp,
                    maxLength = 500
                )
                SabaDropdownField("Próxima acción", nextAction, nextActionOptions, { nextAction = it })
                SabaDropdownField("Responsable", owner, ownerOptions, { owner = it })

                SabaCard(container = SabaColors.Gray, stroke = SabaColors.Gray) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Person, contentDescription = null,
                            tint = SabaColors.Ink, modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Registrado por", style = SabaType.Caption)
                            Text(registeredBy, style = SabaType.CardTitle)
                            Text(registeredAt, style = SabaType.Caption)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}



@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "S07 Incidente")
@Composable
private fun PreviewS07() = SabaTheme { IncidentDetailScreen() }

@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "S08 Soporte")
@Composable
private fun PreviewS08() = SabaTheme { SupportRequestScreen() }

@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "S09 Gestión")
@Composable
private fun PreviewS09() = SabaTheme { RegisterManagementScreen() }