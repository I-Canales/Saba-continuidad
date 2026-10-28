package com.example.sabacontinuidad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sabacontinuidad.data.Incidente
import com.example.sabacontinuidad.data.IncidenteRepository
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
    val avisoDuplicado: Boolean = false
) {
    val errorEquipo: String?
        get() = when {
            equipo.isBlank() -> "Ingresa el equipo"
            equipo.trim().length < 3 -> "Mínimo 3 caracteres"
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

    fun onEquipo(texto: String) = _form.update { it.copy(equipo = texto.take(40)) }
    fun onArea(valor: String) = _form.update { it.copy(area = valor) }
    fun onProblema(valor: String) = _form.update { it.copy(problema = valor) }
    fun onDescripcion(texto: String) = _form.update { it.copy(descripcion = texto.take(300)) }

    fun agregarDictado(texto: String) = _form.update {
        it.copy(descripcion = "${it.descripcion} $texto".trim().take(300))
    }

    fun enviar(alGuardar: () -> Unit) {
        val f = _form.value

        if (!f.valido) {
            _form.update { it.copy(intentoEnviar = true) }
            return
        }

        val hayDuplicado = incidentes.value.any {
            it.equipo.equals(f.equipo.trim(), ignoreCase = true) && it.problema == f.problema
        }

        if (hayDuplicado) {
            _form.update { it.copy(avisoDuplicado = true) }
            return
        }

        guardar(f, alGuardar)
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

    private fun guardar(f: ReporteForm, alGuardar: () -> Unit) {
        viewModelScope.launch {
            repositorio.guardar(
                Incidente(
                    id = System.currentTimeMillis(),
                    equipo = f.equipo.trim(),
                    area = f.area,
                    problema = f.problema,
                    descripcion = f.descripcion.trim(),
                    fecha = System.currentTimeMillis()
                )
            )
            _form.value = ReporteForm()
            alGuardar()
        }
    }
}