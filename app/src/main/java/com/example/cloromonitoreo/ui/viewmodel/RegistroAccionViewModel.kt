package com.example.cloromonitoreo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloromonitoreo.data.local.entity.AccionCorrectivaEntity
import com.example.cloromonitoreo.data.local.entity.MedicionEntity
import com.example.cloromonitoreo.data.model.calcularEstadoCloro
import com.example.cloromonitoreo.data.repository.CloroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RegistroAccionUiState(
    val puntoId: String = "",
    val granja: String = "Granja Melipilla",
    val galpon: String = "Galpón 1",
    val descripcion: String = "",
    val responsable: String = "",
    val nuevoPpm: String = "",
    val errorDescripcion: String? = null,
    val errorResponsable: String? = null,
    val errorNuevoPpm: String? = null,
    val guardadoExitoso: Boolean = false
)

class RegistroAccionViewModel(
    private val repository: CloroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroAccionUiState())
    val uiState: StateFlow<RegistroAccionUiState> = _uiState.asStateFlow()

    fun inicializarFormulario(puntoId: String, granja: String, galpon: String, responsable: String) {
        _uiState.update {
            it.copy(
                puntoId = puntoId,
                granja = granja,
                galpon = galpon,
                responsable = responsable,
                descripcion = "",
                nuevoPpm = "",
                errorDescripcion = null,
                errorResponsable = null,
                errorNuevoPpm = null,
                guardadoExitoso = false
            )
        }
    }

    fun onDescripcionChange(desc: String) {
        _uiState.update { it.copy(descripcion = desc, errorDescripcion = null) }
    }

    fun onResponsableChange(resp: String) {
        _uiState.update { it.copy(responsable = resp, errorResponsable = null) }
    }

    fun onNuevoPpmChange(ppm: String) {
        _uiState.update { it.copy(nuevoPpm = ppm, errorNuevoPpm = null) }
    }

    fun guardarAccionCorrectiva(onSuccess: () -> Unit) {
        val desc = _uiState.value.descripcion.trim()
        val resp = _uiState.value.responsable.trim()
        val ppmStr = _uiState.value.nuevoPpm.trim().replace(',', '.')
        val ppmVal = ppmStr.toDoubleOrNull()

        var hayError = false
        var errDesc: String? = null
        var errResp: String? = null
        var errPpm: String? = null

        if (desc.isBlank()) {
            errDesc = "Debe describir la acción correctiva realizada"
            hayError = true
        }

        if (resp.isBlank()) {
            errResp = "El responsable es obligatorio"
            hayError = true
        }

        if (ppmVal == null || ppmVal < 0.0) {
            errPpm = "Ingrese un valor válido de cloro (ppm)"
            hayError = true
        }

        if (hayError) {
            _uiState.update {
                it.copy(
                    errorDescripcion = errDesc,
                    errorResponsable = errResp,
                    errorNuevoPpm = errPpm
                )
            }
            return
        }

        viewModelScope.launch {
            val formatoFecha = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val ahora = formatoFecha.format(Date())

            val accion = AccionCorrectivaEntity(
                alertaId = "ALT-${_uiState.value.puntoId}",
                puntoId = _uiState.value.puntoId,
                descripcion = desc,
                responsable = resp,
                nuevoNivelPpm = ppmVal!!,
                fechaHora = ahora
            )
            repository.registrarAccionCorrectiva(accion)

            // Registramos también la nueva medición restablecida
            val estado = calcularEstadoCloro(ppmVal)
            val medicion = MedicionEntity(
                puntoId = _uiState.value.puntoId,
                granja = _uiState.value.granja,
                galpon = _uiState.value.galpon,
                nivelCloroPpm = ppmVal,
                estado = estado.etiqueta,
                fechaHora = ahora
            )
            repository.registrarMedicion(medicion)

            _uiState.update { it.copy(guardadoExitoso = true) }
            onSuccess()
        }
    }
}