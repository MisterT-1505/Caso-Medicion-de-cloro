package com.example.cloromonitoreo

import com.example.cloromonitoreo.data.model.CloroEstado
import com.example.cloromonitoreo.data.model.calcularEstadoCloro
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun calcularEstadoCloro_valorBajoCritico_retornaCritico() {
        // Valores inferiores a 2.00 ppm deben activar estado Crítico
        val resultado = calcularEstadoCloro(1.50)
        assertEquals(CloroEstado.CRITICO, resultado)
    }

    @Test
    fun calcularEstadoCloro_valorBajoAdvertencia_retornaAdvertencia() {
        // Rango de 2.00 a menos de 3.00 ppm
        val resultado = calcularEstadoCloro(2.40)
        assertEquals(CloroEstado.ADVERTENCIA, resultado)
    }

    @Test
    fun calcularEstadoCloro_valorOptimoNormal_retornaNormal() {
        // Rango recomendado de 3.00 a 5.00 ppm
        val resultado = calcularEstadoCloro(3.80)
        assertEquals(CloroEstado.NORMAL, resultado)
    }

    @Test
    fun calcularEstadoCloro_valorAltoAdvertencia_retornaAdvertencia() {
        // Rango superior a 5.00 hasta 6.00 ppm
        val resultado = calcularEstadoCloro(5.50)
        assertEquals(CloroEstado.ADVERTENCIA, resultado)
    }

    @Test
    fun calcularEstadoCloro_valorAltoCritico_retornaCritico() {
        // Valores superiores a 6.00 ppm
        val resultado = calcularEstadoCloro(6.80)
        assertEquals(CloroEstado.CRITICO, resultado)
    }
}