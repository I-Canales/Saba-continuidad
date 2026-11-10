package com.example.sabacontinuidad.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.sabacontinuidad.ui.theme.SabaColors
import com.example.sabacontinuidad.ui.theme.SabaType








@Composable
fun SabaTopBar(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onMenu: (() -> Unit)? = null,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Outlined.ChevronLeft, contentDescription = "Volver", tint = SabaColors.Ink)
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = SabaType.Title)
            Text(subtitle, style = SabaType.Subtitle)
        }
        if (onMenu != null) {
            IconButton(onClick = onMenu) {
                Icon(Icons.Outlined.MoreVert, contentDescription = "Más opciones", tint = SabaColors.Ink)
            }
        }
    }
}



@Composable
fun SabaCard(
    modifier: Modifier = Modifier,
    container: Color = Color.White,
    stroke: Color = SabaColors.Border,
    padding: PaddingValues = PaddingValues(12.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    val base = modifier
        .fillMaxWidth()
        .clip(shape)
        .background(container, shape)
        .border(1.dp, stroke, shape)
    Column(
        (if (onClick != null) base.clickable(onClick = onClick) else base).padding(padding),
        content = content
    )
}


@Composable
fun SabaChip(text: String, icon: ImageVector, container: Color, content: Color) {
    Row(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(container)
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = content, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(5.dp))
        Text(text, style = SabaType.Chip.copy(color = content))
    }
}



enum class ActionStyle { Filled, Outlined, Danger }

@Composable
fun SabaActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ActionStyle = ActionStyle.Outlined,
    chevron: Boolean = true,
) {
    val (bg, fg, stroke) = when (style) {
        ActionStyle.Filled -> Triple(SabaColors.Green, Color.White, SabaColors.Green)
        ActionStyle.Outlined -> Triple(Color.White, SabaColors.Green, SabaColors.Green)
        ActionStyle.Danger -> Triple(SabaColors.RedSoft, SabaColors.Red, SabaColors.RedBorder)
    }
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(shape)
            .background(bg, shape)
            .border(1.dp, stroke, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (chevron) Arrangement.SpaceBetween else Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(text, style = SabaType.Button.copy(color = fg))
        }
        if (chevron) {
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = fg, modifier = Modifier.size(18.dp))
        }
    }
}

/** Botón pequeño "Llamar" de las tarjetas de contacto. */
@Composable
fun SabaSmallOutlinedButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        Modifier
            .clip(shape)
            .background(Color.White, shape)
            .border(BorderStroke(1.dp, SabaColors.Green), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = SabaColors.Green, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, style = SabaType.Chip.copy(color = SabaColors.Green))
    }
}



@Composable
fun SabaDropdownField(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(8.dp)
    Column(modifier.fillMaxWidth()) {
        Text(label, style = SabaType.FieldLabel)
        Spacer(Modifier.height(4.dp))
        Box {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(shape)
                    .background(Color.White, shape)
                    .border(1.dp, SabaColors.Border, shape)
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(value, style = SabaType.Field)
                Icon(
                    Icons.Outlined.KeyboardArrowDown, contentDescription = null,
                    tint = SabaColors.Ink, modifier = Modifier.size(18.dp)
                )
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, style = SabaType.Field) },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SabaTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minHeight: Dp = 40.dp,
    maxLength: Int? = null,
) {
    val shape = RoundedCornerShape(8.dp)
    Column(modifier.fillMaxWidth()) {
        Text(label, style = SabaType.FieldLabel)
        Spacer(Modifier.height(4.dp))
        BasicTextField(
            value = value,
            onValueChange = { if (maxLength == null || it.length <= maxLength) onValueChange(it) },
            singleLine = singleLine,
            textStyle = SabaType.Field,
            cursorBrush = SolidColor(SabaColors.Green),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = minHeight)
                        .clip(shape)
                        .background(Color.White, shape)
                        .border(1.dp, SabaColors.Border, shape)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
                ) { inner() }
            }
        )
        if (maxLength != null) {
            Text(
                "${value.length}/$maxLength",
                style = SabaType.Caption,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
            )
        }
    }
}

private data class NavItem(val label: String, val icon: ImageVector)

private val navItems = listOf(
    NavItem("Inicio", Icons.Outlined.Home),
    NavItem("Incidentes", Icons.Outlined.WarningAmber),
    NavItem("Manuales", Icons.AutoMirrored.Outlined.MenuBook),
    NavItem("Perfil", Icons.Outlined.PersonOutline),
)


@Composable
fun SabaBottomBar(selected: Int, onSelect: (Int) -> Unit) {
    Column(Modifier.background(Color.White)) {
        HorizontalDivider(color = SabaColors.Border)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            navItems.forEachIndexed { index, item ->
                val active = index == selected
                val tint = if (active) SabaColors.Green else SabaColors.Muted
                Column(
                    Modifier
                        .clickable { onSelect(index) }
                        .padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(item.icon, contentDescription = item.label, tint = tint, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.height(2.dp))
                    Text(item.label, style = SabaType.Nav.copy(color = tint))
                    Spacer(Modifier.height(3.dp))
                    Box(
                        Modifier
                            .width(24.dp)
                            .height(2.dp)
                            .background(if (active) SabaColors.Green else Color.Transparent)
                    )
                }
            }
        }
    }
}