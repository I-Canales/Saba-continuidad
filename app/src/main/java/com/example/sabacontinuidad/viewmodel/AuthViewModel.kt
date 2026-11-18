package com.example.sabacontinuidad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sabacontinuidad.data.AuthRepository
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val cargando: Boolean = true,
    val usuario: String? = null,
    val mensaje: String? = null
)

class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repositorio = AuthRepository()

    private val _estado = MutableStateFlow(AuthUiState())

    val estado: StateFlow<AuthUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.estadoSesion.collect { sesion ->
                when (sesion) {
                    SessionStatus.Initializing -> {
                        _estado.update {
                            it.copy(cargando = true)
                        }
                    }

                    is SessionStatus.Authenticated -> {
                        val usuario = sesion.session.user?.email
                            ?: sesion.session.user?.id
                            ?: "Usuario Microsoft"

                        _estado.value = AuthUiState(
                            cargando = false,
                            usuario = usuario
                        )
                    }

                    is SessionStatus.NotAuthenticated -> {
                        _estado.update {
                            it.copy(
                                cargando = false,
                                usuario = null
                            )
                        }
                    }

                    is SessionStatus.RefreshFailure -> {
                        _estado.value = AuthUiState(
                            cargando = false,
                            mensaje = "No se pudo renovar la sesión. " +
                                    "Revisa tu conexión e inicia sesión nuevamente."
                        )
                    }
                }
            }
        }
    }

    fun iniciarSesion() {
        if (_estado.value.cargando) return

        _estado.update {
            it.copy(
                cargando = true,
                mensaje = null
            )
        }

        viewModelScope.launch {
            try {
                repositorio.iniciarSesionMicrosoft()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                reportarErrorLogin(
                    error.message
                        ?: "No se pudo iniciar sesión con Microsoft."
                )
            } finally {
                _estado.update {
                    it.copy(cargando = false)
                }
            }
        }
    }

    fun cerrarSesion() {
        if (_estado.value.cargando) return

        _estado.update {
            it.copy(
                cargando = true,
                mensaje = null
            )
        }

        viewModelScope.launch {
            try {
                repositorio.cerrarSesion()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _estado.update {
                    it.copy(
                        mensaje = error.message
                            ?: "No se pudo cerrar la sesión."
                    )
                }
            } finally {
                _estado.update {
                    it.copy(cargando = false)
                }
            }
        }
    }

    fun cerrarMensaje() {
        _estado.update {
            it.copy(mensaje = null)
        }
    }

    fun reportarErrorLogin(mensaje: String?) {
        _estado.update {
            it.copy(
                cargando = false,
                mensaje = mensaje
                    ?: "Microsoft no pudo completar el inicio de sesión."
            )
        }
    }
}