package com.example.sabacontinuidad.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = VerdeSaba,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB7F0D8),
    onPrimaryContainer = Color(0xFF002117),
    background = FondoSaba,
    onBackground = TextoSaba,
    surface = Color.White,
    onSurface = TextoSaba,
    surfaceVariant = Color(0xFFDCE5E0),
    outline = BordeSaba
)

private val EsquemaOscuro = darkColorScheme(
    primary = Color(0xFF7FD6B6),
    onPrimary = Color(0xFF00382A),
    primaryContainer = Color(0xFF00513D),
    onPrimaryContainer = Color(0xFFB7F0D8),
    background = Color(0xFF0F1512),
    onBackground = Color(0xFFDFE4E0),
    surface = Color(0xFF0F1512),
    onSurface = Color(0xFFDFE4E0),
    surfaceVariant = Color(0xFF3F4945),
    outline = Color(0xFF89938E)
)

@Composable
fun SabaContinuidadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        content = content
    )
}