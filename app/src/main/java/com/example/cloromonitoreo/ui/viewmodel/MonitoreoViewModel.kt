package com.example.cloromonitoreo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloromonitoreo.data.local.entity.AccionCorrectivaEntity
import com.example.cloromonitoreo.data.local.entity.MedicionEntity
import com.example.cloromonitoreo.data.model.calcularEstadoCloro
import com.example.cloromonitoreo.data.repository.CloroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MonitoreoUiState(
    val granjaSeleccionada: String = "Granja Melipilla",
    val granjasDisponibles: List<String> = listOf("Granja Melipilla", "Granja Paine", "Granja Talagante"),
    val puntoSeleccionadoId: String? = null,
    val periodoHistorial: String = "24 horas",
    val sincronizandoApi: Boolean = false,
    val mensajeApi: String? = null
)

class MonitoreoViewModel(
    private val repository: CloroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonitoreoUiState())
    val uiState: StateFlow<MonitoreoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.sembrarDatosIniciales()
        }
    }

    val mediciones: StateFlow<List<MedicionEntity>> = repository.todasLasMediciones
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val accionesCorrectivas: StateFlow<List<AccionCorrectivaEntity>> = repository.todasLasAcciones
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun seleccionarGranja(granja: String) {
        _uiState.update { it.copy(granjaSeleccionada = granja, mensajeApi = null) }
    }

    fun seleccionarPunto(puntoId: String) {
        _uiState.update { it.copy(puntoSeleccionadoId = puntoId) }
    }

    fun seleccionarPeriodoHistorial(periodo: String) {
        _uiState.update { it.copy(periodoHistorial = periodo) }
    }

    fun sincronizarDesdeApi() {
        val granjaActual = _uiState.value.granjaSeleccionada
        viewModelScope.launch {
            _uiState.update { it.copy(sincronizandoApi = true, mensajeApi = "Consultando sensores...") }
            val criticas = repository.sincronizarMedicionesDesdeApi(granjaActual)
            val resumen = if (criticas > 0) {
                "API sincronizada: $criticas alerta(s) detectada(s)"
            } else {
                "API sincronizada: Puntos actualizados con normalidad"
            }
            _uiState.update { it.copy(sincronizandoApi = false, mensajeApi = resumen) }
        }
    }

    fun registrarNuevaMedicion(puntoId: String, granja: String, galpon: String, nivelPpm: Double) {
        viewModelScope.launch {
            val estadoCalculado = calcularEstadoCloro(nivelPpm)
            val formatoFecha = SimpleDateFormat("HH:mm", Locale.getDefault())
            val nuevaMedicion = MedicionEntity(
                puntoId = puntoId,
                granja = granja,
                galpon = galpon,
                nivelCloroPpm = nivelPpm,
                estado = estadoCalculado.etiqueta,
                fechaHora = formatoFecha.format(Date())
            )
            repository.registrarMedicion(nuevaMedicion)
        }
    }

    fun validarAccionPorSupervisor(accion: AccionCorrectivaEntity) {
        viewModelScope.launch {
            repository.actualizarAccionCorrectiva(
                accion.copy(validadaPorSupervisor = true)
            )
        }
    }

    fun eliminarAccionCorrectiva(accion: AccionCorrectivaEntity) {
        viewModelScope.launch {
            repository.eliminarAccionCorrectiva(accion)
        }
    }
}