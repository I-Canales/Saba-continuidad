package com.example.sabacontinuidad.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.sabacontinuidad.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AuthUiState(
    val cargando: Boolean = true,
    val usuario: String? = null,
    val mensaje: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = AuthRepository(application.applicationContext)

    private val _estado = MutableStateFlow(AuthUiState())
    val estado: StateFlow<AuthUiState> = _estado.asStateFlow()

    init {
        repositorio.iniciar(
            cuandoListo = { usuario ->
                _estado.update { it.copy(cargando = false, usuario = usuario) }
            },
            cuandoError = { mensaje ->
                _estado.update { it.copy(cargando = false, mensaje = mensaje) }
            }
        )
    }

    fun iniciarSesion(activity: Activity) {
        if (!repositorio.listo) {
            _estado.update {
                it.copy(mensaje = "Microsoft todavía se está iniciando. Intenta nuevamente.")
            }
            return
        }

        _estado.update { it.copy(cargando = true) }

        repositorio.iniciarSesion(
            activity = activity,
            cuandoExito = { usuario ->
                _estado.update { it.copy(cargando = false, usuario = usuario) }
            },
            cuandoError = { mensaje ->
                _estado.update { it.copy(cargando = false, mensaje = mensaje) }
            },
            cuandoCancela = {
                _estado.update {
                    it.copy(cargando = false, mensaje = "Se canceló el inicio de sesión.")
                }
            }
        )
    }

    fun cerrarSesion() {
        repositorio.cerrarSesion(
            cuandoListo = { _estado.update { it.copy(usuario = null) } },
            cuandoError = { mensaje -> _estado.update { it.copy(mensaje = mensaje) } }
        )
    }

    fun mostrarMensaje(mensaje: String) {
        _estado.update { it.copy(mensaje = mensaje) }
    }

    fun cerrarMensaje() {
        _estado.update { it.copy(mensaje = null) }
    }
}