package com.example.cloromonitoreo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mediciones")
data class MedicionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val puntoId: String,
    val granja: String,
    val galpon: String,
    val nivelCloroPpm: Double,
    val estado: String,
    val fechaHora: String
)