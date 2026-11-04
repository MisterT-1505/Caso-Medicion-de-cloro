package com.example.cloromonitoreo.data.repository

import com.example.cloromonitoreo.data.local.entity.MedicionEntity
import com.example.cloromonitoreo.data.model.calcularEstadoCloro
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class CloroApiService {
    suspend fun obtenerMedicionesEnLinea(granja: String): List<MedicionEntity> {
        delay(800) // Simula la carga de internet
        val prefijo = when (granja) {
            "Granja Melipilla" -> "G1"
            "Granja Paine" -> "G2"
            else -> "G3"
        }
        val horaActual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        return (1..4).map { numeroGalpon ->
            val valorAleatorio = (Random.nextDouble(1.50, 6.50) * 100.0).toInt() / 100.0
            val estado = calcularEstadoCloro(valorAleatorio)

            MedicionEntity(
                puntoId = "$prefijo-P$numeroGalpon",
                granja = granja,
                galpon = "Galpón $numeroGalpon",
                nivelCloroPpm = valorAleatorio,
                estado = estado.etiqueta,
                fechaHora = horaActual
            )
        }
    }
}