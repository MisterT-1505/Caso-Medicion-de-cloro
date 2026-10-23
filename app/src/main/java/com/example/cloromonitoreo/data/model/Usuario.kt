package com.example.cloromonitoreo.data.model

enum class RolUsuario {
    OPERARIO,
    SUPERVISOR,
    JEFATURA
}

data class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val rol: RolUsuario,
    val granjaAsignada: String
)