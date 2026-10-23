package com.example.cloromonitoreo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloromonitoreo.data.model.RolUsuario
import com.example.cloromonitoreo.data.model.Usuario
import com.example.cloromonitoreo.data.repository.PreferenciasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val correo: String = "",
    val contrasena: String = "",
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val usuarioAutenticado: Usuario? = null,
    val isLoading: Boolean = false,
    val errorGeneral: String? = null
)

class AuthViewModel(
    private val preferenciasRepository: PreferenciasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Cuentas simuladas para demostración del MVP
    private val usuariosFicticios = listOf(
        Usuario("U1", "Juan Pérez", "operario@empresa.cl", RolUsuario.OPERARIO, "Granja Melipilla"),
        Usuario("U2", "Carlos Gómez", "supervisor@empresa.cl", RolUsuario.SUPERVISOR, "Granja Melipilla"),
        Usuario("U3", "Marcela Silva", "jefatura@empresa.cl", RolUsuario.JEFATURA, "Todas las granjas")
    )

    fun onCorreoChange(correo: String) {
        _uiState.update { it.copy(correo = correo, errorCorreo = null, errorGeneral = null) }
    }

    fun onContrasenaChange(contrasena: String) {
        _uiState.update { it.copy(contrasena = contrasena, errorContrasena = null, errorGeneral = null) }
    }

    fun iniciarSesion(onSuccess: (RolUsuario) -> Unit) {
        val correo = _uiState.value.correo.trim()
        val clave = _uiState.value.contrasena.trim()

        var hayError = false
        var errorCorreo: String? = null
        var errorClave: String? = null

        if (correo.isBlank()) {
            errorCorreo = "El correo electrónico es obligatorio"
            hayError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            errorCorreo = "Formato de correo no válido"
            hayError = true
        }

        if (clave.length < 4) {
            errorClave = "La contraseña debe tener al menos 4 caracteres"
            hayError = true
        }

        if (hayError) {
            _uiState.update { it.copy(errorCorreo = errorCorreo, errorContrasena = errorClave) }
            return
        }

        val usuario = usuariosFicticios.find { it.correo.equals(correo, ignoreCase = true) }

        if (usuario != null) {
            viewModelScope.launch {
                preferenciasRepository.guardarSesion(
                    nombre = usuario.nombre,
                    correo = usuario.correo,
                    rol = usuario.rol.name
                )
                _uiState.update { it.copy(usuarioAutenticado = usuario, errorGeneral = null) }
                onSuccess(usuario.rol)
            }
        } else {
            // Usuario genérico para pruebas rápidas
            val usuarioGenerico = Usuario("U0", "Operario Demo", correo, RolUsuario.OPERARIO, "Granja Melipilla")
            viewModelScope.launch {
                preferenciasRepository.guardarSesion(usuarioGenerico.nombre, usuarioGenerico.correo, usuarioGenerico.rol.name)
                _uiState.update { it.copy(usuarioAutenticado = usuarioGenerico, errorGeneral = null) }
                onSuccess(usuarioGenerico.rol)
            }
        }
    }

    fun cerrarSesion(onComplete: () -> Unit) {
        viewModelScope.launch {
            preferenciasRepository.cerrarSesion()
            _uiState.update { AuthUiState() }
            onComplete()
        }
    }
}