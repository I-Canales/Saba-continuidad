package com.example.sabacontinuidad.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Azure
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.StateFlow

class AuthRepository {
    private val auth = SupabaseClientProvider.client.auth

    val estadoSesion: StateFlow<SessionStatus>
        get() = auth.sessionStatus

    suspend fun iniciarSesionMicrosoft() {
        auth.signInWith(Azure) {
            // Supabase Auth necesita que Microsoft entregue un correo válido.
            scopes.add("email")
        }
    }

    suspend fun cerrarSesion() {
        auth.signOut()
    }
}
