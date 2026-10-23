package com.example.cloromonitoreo.data.repository

import com.example.cloromonitoreo.data.local.dao.AccionCorrectivaDao
import com.example.cloromonitoreo.data.local.dao.MedicionDao
import com.example.cloromonitoreo.data.local.entity.AccionCorrectivaEntity
import com.example.cloromonitoreo.data.local.entity.MedicionEntity
import kotlinx.coroutines.flow.Flow

class CloroRepository(
    private val medicionDao: MedicionDao,
    private val accionCorrectivaDao: AccionCorrectivaDao
) {
    val todasLasMediciones: Flow<List<MedicionEntity>> = medicionDao.obtenerTodas()
    val todasLasAcciones: Flow<List<AccionCorrectivaEntity>> = accionCorrectivaDao.obtenerTodas()

    fun obtenerPorGranja(granja: String): Flow<List<MedicionEntity>> {
        return medicionDao.obtenerPorGranja(granja)
    }

    fun obtenerHistorialPunto(puntoId: String): Flow<List<MedicionEntity>> {
        return medicionDao.obtenerHistorialPunto(puntoId)
    }

    suspend fun registrarMedicion(medicion: MedicionEntity) {
        medicionDao.insertar(medicion)
    }

    suspend fun registrarAccionCorrectiva(accion: AccionCorrectivaEntity) {
        accionCorrectivaDao.insertar(accion)
    }

    suspend fun sembrarDatosIniciales() {
        if (medicionDao.contarMediciones() == 0) {
            val semillas = listOf(
                // Granja Melipilla (4 puntos)
                MedicionEntity(puntoId = "G1-P1", granja = "Granja Melipilla", galpon = "Galpón 1", nivelCloroPpm = 3.80, estado = "Normal", fechaHora = "10:00"),
                MedicionEntity(puntoId = "G1-P2", granja = "Granja Melipilla", galpon = "Galpón 2", nivelCloroPpm = 2.40, estado = "Advertencia", fechaHora = "10:05"),
                MedicionEntity(puntoId = "G1-P3", granja = "Granja Melipilla", galpon = "Galpón 3", nivelCloroPpm = 1.60, estado = "Crítico", fechaHora = "10:10"),
                MedicionEntity(puntoId = "G1-P4", granja = "Granja Melipilla", galpon = "Galpón 4", nivelCloroPpm = 4.20, estado = "Normal", fechaHora = "10:15"),

                // Granja Paine (4 puntos)
                MedicionEntity(puntoId = "G2-P1", granja = "Granja Paine", galpon = "Galpón 1", nivelCloroPpm = 4.00, estado = "Normal", fechaHora = "09:30"),
                MedicionEntity(puntoId = "G2-P2", granja = "Granja Paine", galpon = "Galpón 2", nivelCloroPpm = 6.40, estado = "Crítico", fechaHora = "09:35"),
                MedicionEntity(puntoId = "G2-P3", granja = "Granja Paine", galpon = "Galpón 3", nivelCloroPpm = 3.50, estado = "Normal", fechaHora = "09:40"),
                MedicionEntity(puntoId = "G2-P4", granja = "Granja Paine", galpon = "Galpón 4", nivelCloroPpm = 5.30, estado = "Advertencia", fechaHora = "09:45"),

                // Granja Talagante (4 puntos)
                MedicionEntity(puntoId = "G3-P1", granja = "Granja Talagante", galpon = "Galpón 1", nivelCloroPpm = 3.90, estado = "Normal", fechaHora = "11:00"),
                MedicionEntity(puntoId = "G3-P2", granja = "Granja Talagante", galpon = "Galpón 2", nivelCloroPpm = 3.70, estado = "Normal", fechaHora = "11:05"),
                MedicionEntity(puntoId = "G3-P3", granja = "Granja Talagante", galpon = "Galpón 3", nivelCloroPpm = 2.20, estado = "Advertencia", fechaHora = "11:10"),
                MedicionEntity(puntoId = "G3-P4", granja = "Granja Talagante", galpon = "Galpón 4", nivelCloroPpm = 4.50, estado = "Normal", fechaHora = "11:15")
            )
            medicionDao.insertarTodas(semillas)
        }
    }
}