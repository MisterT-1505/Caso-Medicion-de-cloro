package com.example.cloromonitoreo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cloromonitoreo.data.local.dao.AccionCorrectivaDao
import com.example.cloromonitoreo.data.local.dao.MedicionDao
import com.example.cloromonitoreo.data.local.entity.AccionCorrectivaEntity
import com.example.cloromonitoreo.data.local.entity.MedicionEntity

@Database(
    entities = [MedicionEntity::class, AccionCorrectivaEntity::class],
    version = 2, // Subimos a 2 tras agregar imagenUri
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicionDao(): MedicionDao
    abstract fun accionCorrectivaDao(): AccionCorrectivaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cloro_monitoreo_db"
                )
                    .fallbackToDestructiveMigration() // Evita crashes si cambia el esquema
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}