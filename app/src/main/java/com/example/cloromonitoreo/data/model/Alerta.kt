package com.example.cloromonitoreo.data.model

enum class TipoDesviacion(val descripcion: String) {
    CLORO_INSUFICIENTE("Cloro insuficiente (< 2.00 ppm)"),
    CLORO_EXCESIVO("Cloro excesivo (> 6.00 ppm)")
}

data class Alerta(
    val id: String,
    val puntoId: String,
    val granja: String,
    val galpon: String,
    val nivelPpm: Double,
    val tipoDesviacion: TipoDesviacion,
    val nivelGravedad: CloroEstado,
    val fechaHora: String,
    val resuelta: Boolean = false
)

data class AccionCorrectiva(
    val id: Int = 0,
    val alertaId: String,
    val puntoId: String,
    val descripcion: String,
    val responsable: String,
    val fechaHora: String,
    val nuevoNivelPpm: Double,
    val validadaPorSupervisor: Boolean = false
)