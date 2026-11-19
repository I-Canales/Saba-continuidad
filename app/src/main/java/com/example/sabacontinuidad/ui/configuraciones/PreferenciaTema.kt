package com.example.sabacontinuidad.ui.configuraciones

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

enum class ModoTema(val titulo: String, val descripcion: String) {
    CLARO(
        "Claro",
        "Interfaz clara, ideal para entornos bien iluminados."
    ),
    OSCURO(
        "Oscuro",
        "Interfaz oscura, ideal para entornos con poca luz."
    ),
    SISTEMA(
        "Según el sistema",
        "La apariencia cambia con la configuración del teléfono."
    )
}

class PreferenciaTema(context: Context) {
    private val preferencias =
        context.getSharedPreferences("apariencia", Context.MODE_PRIVATE)

    fun leer(): ModoTema {
        val guardado = preferencias.getString("modo", null)
        return ModoTema.entries.firstOrNull { it.name == guardado } ?: ModoTema.SISTEMA
    }

    fun guardar(modo: ModoTema) {
        preferencias.edit().putString("modo", modo.name).apply()
    }
}

@Composable
fun ModoTema.usaTemaOscuro(): Boolean = when (this) {
    ModoTema.CLARO -> false
    ModoTema.OSCURO -> true
    ModoTema.SISTEMA -> isSystemInDarkTheme()
}