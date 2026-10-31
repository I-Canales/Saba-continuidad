package com.example.sabacontinuidad

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.sabacontinuidad.data.SupabaseClientProvider
import com.example.sabacontinuidad.navigation.AppNavigation
import com.example.sabacontinuidad.ui.theme.SabaContinuidadTheme
import com.example.sabacontinuidad.viewmodel.AuthViewModel
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        manejarDeeplink(intent)

        setContent {
            SabaContinuidadTheme {
                AppNavigation()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        manejarDeeplink(intent)
    }

    private fun manejarDeeplink(intent: Intent) {
        SupabaseClientProvider.client.handleDeeplinks(
            intent = intent,
            onError = { error ->
                authViewModel.reportarErrorLogin(error.message)
            }
        )
    }
}