package com.example.sabacontinuidad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sabacontinuidad.data.AuthRepository
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val cargando: Boolean = true,
    val usuario: String? = null,
    val mensaje: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repositorio = AuthRepository()

    private val _estado = MutableStateFlow(AuthUiState())
    val estado: StateFlow<AuthUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.estadoSesion.collect { sesion ->
                when (sesion) {
                    SessionStatus.Initializing -> _estado.value = _estado.value.copy(cargando = true)
                    is SessionStatus.Authenticated -> {
                        val usuario = sesion.session.user?.email
                            ?: sesion.session.user?.id
                            ?: "Usuario Microsoft"
                        _estado.value = AuthUiState(cargando = false, usuario = usuario)
                    }
                    is SessionStatus.NotAuthenticated -> {
                        _estado.value = AuthUiState(cargando = false)
                    }
                    is SessionStatus.RefreshFailure -> {
                        _estado.value = AuthUiState(
                            cargando = false,
                            mensaje = "La sesión venció. Inicia sesión nuevamente."
                        )
                    }
                }
            }
        }
    }

    fun iniciarSesion() {
        if (_estado.value.cargando) return
        _estado.value = _estado.value.copy(cargando = true, mensaje = null)
        viewModelScope.launch {
            runCatching { repositorio.iniciarSesionMicrosoft() }
                .onFailure { error ->
                    _estado.value = _estado.value.copy(
                        cargando = false,
                        mensaje = error.message ?: "No se pudo iniciar sesión con Microsoft."
                    )
                }
                .onSuccess {
                    // El inicio continúa en el navegador; el deeplink actualizará el estado al volver.
                    if (_estado.value.usuario == null) {
                        _estado.value = _estado.value.copy(cargando = false)
                    }
                }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            runCatching { repositorio.cerrarSesion() }
                .onFailure { error ->
                    _estado.value = _estado.value.copy(
                        mensaje = error.message ?: "No se pudo cerrar la sesión."
                    )
                }
        }
    }

    fun cerrarMensaje() {
        _estado.value = _estado.value.copy(mensaje = null)
    }

    fun reportarErrorLogin(mensaje: String?) {
        _estado.value = _estado.value.copy(
            cargando = false,
            mensaje = mensaje ?: "Microsoft no pudo completar el inicio de sesión."
        )
    }
}
