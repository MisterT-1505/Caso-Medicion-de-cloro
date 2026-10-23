package com.example.cloromonitoreo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cloromonitoreo.data.local.entity.AccionCorrectivaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccionCorrectivaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(accion: AccionCorrectivaEntity)

    @Query("SELECT * FROM acciones_correctivas ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<AccionCorrectivaEntity>>

    @Query("SELECT * FROM acciones_correctivas WHERE puntoId = :puntoId ORDER BY id DESC")
    fun obtenerPorPunto(puntoId: String): Flow<List<AccionCorrectivaEntity>>
}