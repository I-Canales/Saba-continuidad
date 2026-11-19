package com.example.sabacontinuidad

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.sabacontinuidad.data.SupabaseClientProvider
import com.example.sabacontinuidad.navigation.AppNavigation
import com.example.sabacontinuidad.ui.configuraciones.PreferenciaTema
import com.example.sabacontinuidad.ui.configuraciones.usaTemaOscuro
import com.example.sabacontinuidad.ui.theme.SabaContinuidadTheme
import com.example.sabacontinuidad.viewmodel.AuthViewModel
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manejarDeeplink(intent)

        setContent {
            val preferenciaTema = remember {
                PreferenciaTema(applicationContext)
            }

            var modo by remember {
                mutableStateOf(preferenciaTema.leer())
            }

            SabaContinuidadTheme(darkTheme = modo.usaTemaOscuro()) {
                AppNavigation(
                    authVm = authViewModel,
                    modo = modo,
                    alElegirModo = { nuevoModo ->
                        modo = nuevoModo
                        preferenciaTema.guardar(nuevoModo)
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        manejarDeeplink(intent)
    }

    private fun manejarDeeplink(intent: Intent) {
        val uri = intent.data ?: return

        if (uri.scheme != "saba" || uri.host != "login") return

        SupabaseClientProvider.client.handleDeeplinks(
            intent = intent,
            onError = { error ->
                authViewModel.reportarErrorLogin(error.message)
            }
        )
    }
}