package com.example.cloromonitoreo.data.model

data class Medicion(
    val id: Int = 0,
    val puntoId: String,
    val granja: String,
    val galpon: String,
    val nivelCloroPpm: Double,
    val estado: CloroEstado,
    val fechaHora: String
)

fun calcularEstadoCloro(ppm: Double): CloroEstado {
    return when {
        ppm < 2.00 -> CloroEstado.CRITICO
        ppm in 2.00..<3.00 -> CloroEstado.ADVERTENCIA
        ppm in 3.00..5.00 -> CloroEstado.NORMAL
        ppm in 5.01..6.00 -> CloroEstado.ADVERTENCIA
        else -> CloroEstado.CRITICO
    }
}