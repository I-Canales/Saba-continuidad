package com.example.sabacontinuidad.ui.incidentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


internal object IncColor {
    val Green = Color(0xFF0F5C45)
    val GreenSoft = Color(0xFFE2F0E7)
    val GreenBorder = Color(0xFFBBD9C6)
    val BlueSoft = Color(0xFFE3EBF6)
    val BlueText = Color(0xFF244A7C)
    val AmberSoft = Color(0xFFFCE9CF)
    val AmberText = Color(0xFF8A4A00)
    val Red = Color(0xFFC62828)
    val Ink = Color(0xFF1A1F1C)
    val Muted = Color(0xFF6B736E)
    val Border = Color(0xFFDDE2DF)
    val Gray = Color(0xFFF0F2F1)
}


enum class TimelineType { Report, Call, Visit }

data class TimelineEntry(
    val time: String,
    val author: String,
    val description: String,
    val type: TimelineType
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
    val timeline: List<TimelineEntry>
) {
    companion object {
        val Demo = IncidentUi(
            code = "INC-024", center = "Demo Centro", equipmentName = "Barrera de salida 2",
            equipmentCode = "SAL-02", date = "5 oct 2026", status = "En atención",
            impact = "Servicio degradado", summary = "Salida 2 no abre. Salida 1 disponible.",
            owner = "Carlos Muñoz · Jefe de parking", nextFollowUp = "17:15",
            ticket = "SAU-082",
            timeline = listOf(
                TimelineEntry("15:50", "María González", "Problema reportado", TimelineType.Report),
                TimelineEntry("16:10", "Carlos Muñoz", "Soporte contactado", TimelineType.Call),
                TimelineEntry("16:40", "Soporte", "Visita en coordinación", TimelineType.Visit)
            )
        )
    }
}

data class SupportContact(
    val name: String,
    val detail: String,
    val kind: String,
    val icon: ImageVector,
    val phone: String
)

data class ManagementForm(
    val contact: String,
    val channel: String,
    val result: String,
    val ticket: String,
    val summary: String,
    val nextAction: String,
    val owner: String
)

@Composable
internal fun IncHeader(title: String, subtitle: String, onBack: () -> Unit, onMenu: () -> Unit = {}) {
    Column(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver", tint = IncColor.Ink)
            }
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = IncColor.Ink)
                Text(subtitle, fontSize = 12.sp, color = IncColor.Muted)
            }
            IconButton(onClick = onMenu) {
                Icon(Icons.Outlined.MoreVert, contentDescription = "Más opciones", tint = IncColor.Ink)
            }
        }
    }
}

@Composable
internal fun IncCard(
    modifier: Modifier = Modifier,
    fill: Color = Color.White,
    outline: Color = IncColor.Border,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val base = modifier.fillMaxWidth().clip(shape).background(fill).border(1.dp, outline, shape)
    Column(
        modifier = (if (onClick != null) base.clickable(onClick = onClick) else base).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp), content = content
    )
}

@Composable
internal fun IncAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    filled: Boolean = false,
    danger: Boolean = false
) {
    val bg = when { danger -> Color(0xFFFDECEC); filled -> IncColor.Green; else -> Color.White }
    val fg = when { danger -> IncColor.Red; filled -> Color.White; else -> IncColor.Green }
    val outline = if (danger) Color(0xFFE9A8A8) else IncColor.Green
    Row(
        Modifier.fillMaxWidth().height(46.dp).clip(RoundedCornerShape(10.dp))
            .background(bg).border(1.dp, outline, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick).padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = fg, modifier = Modifier.size(19.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = fg)
    }
}

@Composable
internal fun IncChip(text: String, icon: ImageVector, bg: Color, fg: Color) {
    Row(
        Modifier.clip(RoundedCornerShape(20.dp)).background(bg)
            .padding(horizontal = 9.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = fg, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(5.dp))
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = fg)
    }
}

@Composable
internal fun IncBottomBar(selected: Int = 1, onSelect: (Int) -> Unit = {}) {
    val labels = listOf("Inicio", "Incidentes", "Manuales", "Perfil")
    val icons = listOf(Icons.Outlined.Home, Icons.Outlined.WarningAmber, Icons.Outlined.MenuBook, Icons.Outlined.PersonOutline)
    Column(Modifier.background(Color.White)) {
        HorizontalDivider(color = IncColor.Border)
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
            labels.forEachIndexed { index, label ->
                Column(Modifier.clickable { onSelect(index) }.padding(horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(icons[index], label, tint = if (index == selected) IncColor.Green else IncColor.Muted, modifier = Modifier.size(22.dp))
                    Text(label, fontSize = 10.sp, color = if (index == selected) IncColor.Green else IncColor.Muted)
                }
            }
        }
    }
}

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
    onNavSelect: (Int) -> Unit = {}
) {
    Scaffold(modifier = modifier, containerColor = Color.White,
        bottomBar = { IncBottomBar(onSelect = onNavSelect) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())) {
            IncHeader("Incidente ${incident.code}", "${incident.center} · ${incident.equipmentName}", onBack, onMenu)
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CalendarToday, null, tint = IncColor.Ink, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(incident.date, color = IncColor.Ink, fontSize = 13.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IncChip(incident.status, Icons.Outlined.AccessTime, IncColor.BlueSoft, IncColor.BlueText)
                    IncChip(incident.impact, Icons.Outlined.Warning, IncColor.AmberSoft, IncColor.AmberText)
                }
                Text(incident.summary, color = IncColor.Ink, fontSize = 13.sp)
                IncCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Person, null, tint = IncColor.Ink)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Responsable del seguimiento", fontSize = 11.sp, color = IncColor.Muted)
                            Text(incident.owner, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IncColor.Ink)
                        }
                    }
                }
                IncCard(fill = IncColor.GreenSoft, outline = IncColor.GreenBorder, onClick = onConfirmVisit) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CalendarMonth, null, tint = IncColor.Green)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Confirmar visita del proveedor", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IncColor.Green)
                            Text("Próximo seguimiento: ${incident.nextFollowUp}", fontSize = 11.sp, color = IncColor.Muted)
                        }
                        Icon(Icons.Outlined.ChevronRight, null, tint = IncColor.Green)
                    }
                }
                Text("Línea de tiempo", color = IncColor.Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                IncCard {
                    incident.timeline.forEachIndexed { index, entry ->
                        TimelineRowInc(entry, index == incident.timeline.lastIndex)
                    }
                }
                IncAction("Registrar actuación", Icons.Outlined.Edit, onRegisterAction)
                IncAction("Solicitar soporte", Icons.Outlined.Headset, onRequestSupport, filled = true)
                IncAction("Escanear QR", Icons.Outlined.QrCodeScanner, onScanQr)
                IncAction("Urgencia", Icons.Outlined.Phone, onUrgency, danger = true)
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun TimelineRowInc(entry: TimelineEntry, last: Boolean) {
    val icon = when (entry.type) {
        TimelineType.Report -> Icons.Outlined.Description
        TimelineType.Call -> Icons.Outlined.Phone
        TimelineType.Visit -> Icons.Outlined.Build
    }
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Box(Modifier.width(20.dp).fillMaxHeight().drawBehind {
            if (!last) drawLine(IncColor.Green, Offset(size.width / 2, 18.dp.toPx()), Offset(size.width / 2, size.height), strokeWidth = 2.dp.toPx())
        }) {
            Box(Modifier.size(20.dp).clip(CircleShape).background(IncColor.Green).align(Alignment.TopCenter), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(13.dp))
            }
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f).padding(bottom = if (last) 0.dp else 14.dp)) {
            Text("${entry.time} · ${entry.author}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = IncColor.Ink)
            Text(entry.description, fontSize = 11.sp, color = IncColor.Muted)
        }
        Icon(icon, null, tint = IncColor.Ink, modifier = Modifier.size(16.dp))
    }
}
