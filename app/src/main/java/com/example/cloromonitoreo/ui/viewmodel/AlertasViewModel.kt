package com.example.cloromonitoreo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloromonitoreo.data.model.Alerta
import com.example.cloromonitoreo.data.model.CloroEstado
import com.example.cloromonitoreo.data.model.TipoDesviacion
import com.example.cloromonitoreo.data.repository.CloroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class AlertasUiState(
    val soloCriticas: Boolean = false,
    val granjaFiltro: String? = null
)

class AlertasViewModel(
    repository: CloroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertasUiState())
    val uiState: StateFlow<AlertasUiState> = _uiState.asStateFlow()

    // Convierte reactivamente las mediciones fuera de rango en objetos Alerta
    val alertasActivas: StateFlow<List<Alerta>> = repository.todasLasMediciones
        .map { mediciones ->
            mediciones.filter { it.nivelCloroPpm < 3.00 || it.nivelCloroPpm > 5.00 }.map { med ->
                val tipo = if (med.nivelCloroPpm < 2.00) {
                    TipoDesviacion.CLORO_INSUFICIENTE
                } else if (med.nivelCloroPpm > 6.00) {
                    TipoDesviacion.CLORO_EXCESIVO
                } else if (med.nivelCloroPpm < 3.00) {
                    TipoDesviacion.CLORO_INSUFICIENTE
                } else {
                    TipoDesviacion.CLORO_EXCESIVO
                }

                val gravedad = if (med.nivelCloroPpm < 2.00 || med.nivelCloroPpm > 6.00) {
                    CloroEstado.CRITICO
                } else {
                    CloroEstado.ADVERTENCIA
                }

                Alerta(
                    id = "ALT-${med.id}",
                    puntoId = med.puntoId,
                    granja = med.granja,
                    galpon = med.galpon,
                    nivelPpm = med.nivelCloroPpm,
                    tipoDesviacion = tipo,
                    nivelGravedad = gravedad,
                    fechaHora = med.fechaHora
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleFiltroCriticas() {
        _uiState.update { it.copy(soloCriticas = !it.soloCriticas) }
    }
}