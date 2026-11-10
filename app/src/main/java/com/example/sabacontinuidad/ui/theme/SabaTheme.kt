package com.example.sabacontinuidad.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


object SabaColors {
    val Green = Color(0xFF0F5C45)
    val GreenSoft = Color(0xFFE2F0E7)
    val GreenBorder = Color(0xFFBBD9C6)

    val BlueSoft = Color(0xFFE3EBF6)
    val BlueText = Color(0xFF244A7C)

    val AmberSoft = Color(0xFFFCE9CF)
    val AmberText = Color(0xFF8A4A00)

    val Red = Color(0xFFC62828)
    val RedSoft = Color(0xFFFDECEC)
    val RedBorder = Color(0xFFE9A8A8)

    val Ink = Color(0xFF1A1F1C)
    val Muted = Color(0xFF6B736E)
    val Border = Color(0xFFDDE2DF)
    val Gray = Color(0xFFF0F2F1)
}

val Inter = FontFamily.Default


object SabaType {
    val Title = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 24.sp, color = SabaColors.Ink
    )
    val Subtitle = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp, color = SabaColors.Muted
    )
    val Section = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Bold,
        fontSize = 16.sp, lineHeight = 20.sp, color = SabaColors.Ink
    )
    val CardTitle = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Bold,
        fontSize = 14.sp, lineHeight = 18.sp, color = SabaColors.Ink
    )
    val Body = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Normal,
        fontSize = 13.sp, lineHeight = 18.sp, color = SabaColors.Ink
    )
    val Caption = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Normal,
        fontSize = 11.sp, lineHeight = 14.sp, color = SabaColors.Muted
    )
    val FieldLabel = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Normal,
        fontSize = 11.sp, lineHeight = 14.sp, color = SabaColors.Muted
    )
    val Field = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 18.sp, color = SabaColors.Ink
    )
    val Button = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 18.sp
    )
    val Chip = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp
    )
    val Nav = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Medium,
        fontSize = 10.sp, lineHeight = 12.sp
    )
}

@Composable
fun SabaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = SabaColors.Green,
            onPrimary = Color.White,
            background = Color.White,
            surface = Color.White,
            onSurface = SabaColors.Ink,
            error = SabaColors.Red,
        ),
        typography = Typography(
            bodyLarge = SabaType.Body,
            bodyMedium = SabaType.Body,
            labelLarge = SabaType.Button,
        ),
        content = content
    )
}