package com.example.sabacontinuidad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sabacontinuidad.data.Incidente
import com.example.sabacontinuidad.data.IncidenteBorrador
import com.example.sabacontinuidad.data.IncidenteRepository
import com.example.sabacontinuidad.data.ResultadoGuardado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReporteForm(
    val equipo: String = "",
    val area: String = "",
    val problema: String = "",
    val descripcion: String = "",
    val intentoEnviar: Boolean = false,
    val avisoDuplicado: Boolean = false,
    val errorServidor: String? = null
) {
    val errorEquipo: String?
        get() = when {
            equipo.isBlank() -> "Ingresa el código del equipo"
            equipo.trim().length < 3 -> "El código debe tener al menos 3 caracteres"
            else -> null
        }

    val errorArea: String?
        get() = if (area.isBlank()) "Selecciona un área" else null

    val errorProblema: String?
        get() = if (problema.isBlank()) "Selecciona el problema" else null

    val errorDescripcion: String?
        get() = if (descripcion.trim().length < 10) {
            "Describe el problema (mínimo 10 caracteres)"
        } else null

    val valido: Boolean
        get() = errorEquipo == null && errorArea == null &&
                errorProblema == null && errorDescripcion == null
}

class IncidenteViewModel(application: Application) : AndroidViewModel(application) {
    private val repositorio = IncidenteRepository(application.applicationContext)

    val incidentes: StateFlow<List<Incidente>> = repositorio.incidentes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _form = MutableStateFlow(ReporteForm())
    val form: StateFlow<ReporteForm> = _form.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _errorCarga = MutableStateFlow<String?>(null)
    val errorCarga: StateFlow<String?> = _errorCarga.asStateFlow()

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    fun onEquipo(texto: String) = _form.update {
        it.copy(equipo = texto.take(80), errorServidor = null)
    }

    fun onArea(valor: String) = _form.update { it.copy(area = valor, errorServidor = null) }
    fun onProblema(valor: String) = _form.update { it.copy(problema = valor, errorServidor = null) }
    fun onDescripcion(texto: String) = _form.update {
        it.copy(descripcion = texto.take(300), errorServidor = null)
    }

    fun agregarDictado(texto: String) = _form.update {
        it.copy(descripcion = "${it.descripcion} $texto".trim().take(300))
    }

    fun cargarIncidentes() {
        viewModelScope.launch {
            _cargando.value = true
            runCatching { repositorio.sincronizar() }
                .onSuccess { sincronizado ->
                    _errorCarga.value = null
                    if (!sincronizado) {
                        _aviso.value = "Sin conexión: mostrando los datos guardados en el celular."
                    }
                }
                .onFailure { error ->
                    _errorCarga.value = error.message ?: "No se pudieron cargar los incidentes."
                }
            _cargando.value = false
        }
    }

    fun enviar(alGuardar: () -> Unit) {
        val actual = _form.value
        if (!actual.valido) {
            _form.update { it.copy(intentoEnviar = true) }
            return
        }

        val duplicado = incidentes.value.any {
            it.equipo.startsWith(actual.equipo.trim(), ignoreCase = true) &&
                    it.problema == actual.problema
        }
        if (duplicado) {
            _form.update { it.copy(avisoDuplicado = true) }
            return
        }

        guardar(actual, alGuardar)
    }

    fun confirmarDuplicado(alGuardar: () -> Unit) {
        guardar(_form.value, alGuardar)
    }

    fun cancelarDuplicado() {
        _form.update { it.copy(avisoDuplicado = false) }
    }

    fun limpiar() {
        _form.value = ReporteForm()
    }

    fun cerrarAviso() {
        _aviso.value = null
    }

    fun limpiarCache() {
        viewModelScope.launch { runCatching { repositorio.limpiarCache() } }
    }

    private fun guardar(formulario: ReporteForm, alGuardar: () -> Unit) {
        viewModelScope.launch {
            _cargando.value = true
            runCatching {
                repositorio.guardar(
                    IncidenteBorrador(
                        codigoEquipo = formulario.equipo,
                        area = formulario.area,
                        problema = formulario.problema,
                        descripcion = formulario.descripcion
                    )
                )
            }.onSuccess { resultado ->
                if (resultado == ResultadoGuardado.PENDIENTE) {
                    _aviso.value = "Sin conexión: el incidente quedó pendiente y se enviará al reconectar."
                }
                _form.value = ReporteForm()
                alGuardar()
            }.onFailure { error ->
                _form.update {
                    it.copy(
                        intentoEnviar = true,
                        avisoDuplicado = false,
                        errorServidor = error.message ?: "No se pudo guardar el incidente."
                    )
                }
            }
            _cargando.value = false
        }
    }
}