package com.example.cloromonitoreo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "acciones_correctivas")
data class AccionCorrectivaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val alertaId: String,
    val puntoId: String,
    val descripcion: String,
    val responsable: String,
    val nuevoNivelPpm: Double,
    val fechaHora: String,
    val imagenUri: String? = null,
    val validadaPorSupervisor: Boolean = false
)