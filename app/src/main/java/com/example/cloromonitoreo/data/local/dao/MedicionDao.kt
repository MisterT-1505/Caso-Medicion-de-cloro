package com.example.cloromonitoreo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cloromonitoreo.data.local.entity.MedicionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(medicion: MedicionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(mediciones: List<MedicionEntity>)

    @Query("SELECT * FROM mediciones ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<MedicionEntity>>

    @Query("SELECT * FROM mediciones WHERE granja = :granja ORDER BY id DESC")
    fun obtenerPorGranja(granja: String): Flow<List<MedicionEntity>>

    @Query("SELECT * FROM mediciones WHERE puntoId = :puntoId ORDER BY id DESC")
    fun obtenerHistorialPunto(puntoId: String): Flow<List<MedicionEntity>>

    @Query("SELECT COUNT(*) FROM mediciones")
    suspend fun contarMediciones(): Int
}