package com.example.cloromonitoreo.data.repository

import com.example.cloromonitoreo.data.local.dao.AccionCorrectivaDao
import com.example.cloromonitoreo.data.local.dao.MedicionDao
import com.example.cloromonitoreo.data.local.entity.AccionCorrectivaEntity
import com.example.cloromonitoreo.data.local.entity.MedicionEntity
import kotlinx.coroutines.flow.Flow

class CloroRepository(
    private val medicionDao: MedicionDao,
    private val accionCorrectivaDao: AccionCorrectivaDao,
    private val apiService: CloroApiService = CloroApiService()
) {
    val todasLasMediciones: Flow<List<MedicionEntity>> = medicionDao.obtenerTodas()
    val todasLasAcciones: Flow<List<AccionCorrectivaEntity>> = accionCorrectivaDao.obtenerTodas()

    fun obtenerPorGranja(granja: String): Flow<List<MedicionEntity>> = medicionDao.obtenerPorGranja(granja)
    fun obtenerHistorialPunto(puntoId: String): Flow<List<MedicionEntity>> = medicionDao.obtenerHistorialPunto(puntoId)

    suspend fun registrarMedicion(medicion: MedicionEntity) {
        medicionDao.insertar(medicion)
    }

    suspend fun sincronizarMedicionesDesdeApi(granja: String): Int {
        val nuevasMediciones = apiService.obtenerMedicionesEnLinea(granja)
        medicionDao.insertarTodas(nuevasMediciones)
        return nuevasMediciones.count { it.nivelCloroPpm < 2.00 || it.nivelCloroPpm > 6.00 }
    }

    suspend fun registrarAccionCorrectiva(accion: AccionCorrectivaEntity) {
        accionCorrectivaDao.insertar(accion)
    }
    suspend fun actualizarAccionCorrectiva(accion: AccionCorrectivaEntity) {
        accionCorrectivaDao.actualizar(accion)
    }
    suspend fun eliminarAccionCorrectiva(accion: AccionCorrectivaEntity) {
        accionCorrectivaDao.eliminar(accion)
    }

    suspend fun sembrarDatosIniciales() {
        if (medicionDao.contarMediciones() == 0) {
            val semillas = listOf(
                MedicionEntity(puntoId = "G1-P1", granja = "Granja Melipilla", galpon = "Galpón 1", nivelCloroPpm = 3.80, estado = "Normal", fechaHora = "10:00"),
                MedicionEntity(puntoId = "G1-P2", granja = "Granja Melipilla", galpon = "Galpón 2", nivelCloroPpm = 2.40, estado = "Advertencia", fechaHora = "10:05"),
                MedicionEntity(puntoId = "G1-P3", granja = "Granja Melipilla", galpon = "Galpón 3", nivelCloroPpm = 1.60, estado = "Crítico", fechaHora = "10:10"),
                MedicionEntity(puntoId = "G1-P4", granja = "Granja Melipilla", galpon = "Galpón 4", nivelCloroPpm = 4.20, estado = "Normal", fechaHora = "10:15")
            )
            medicionDao.insertarTodas(semillas)
        }
    }
}