package com.example.cloromonitoreo.data.model

enum class CloroEstado(val etiqueta: String) {
    NORMAL("Normal"),
    ADVERTENCIA("Advertencia"),
    CRITICO("Crítico")
}

data class PuntoMedicion(
    val id: String,
    val granja: String,
    val galpon: String,
    val ubicacion: String,
    val nivelCloroPpm: Double,
    val estado: CloroEstado,
    val ultimaActualizacion: String
)