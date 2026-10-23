package com.example.cloromonitoreo.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "sesion_usuario")

class PreferenciasRepository(private val context: Context) {
    companion object {
        private val CORREO = stringPreferencesKey("usuario_correo")
        private val NOMBRE = stringPreferencesKey("usuario_nombre")
        private val ROL = stringPreferencesKey("usuario_rol")
    }

    val usuarioCorreo: Flow<String> = context.dataStore.data.map { it[CORREO] ?: "" }
    val usuarioNombre: Flow<String> = context.dataStore.data.map { it[NOMBRE] ?: "" }
    val usuarioRol: Flow<String> = context.dataStore.data.map { it[ROL] ?: "OPERARIO" }

    suspend fun guardarSesion(nombre: String, correo: String, rol: String) {
        context.dataStore.edit { pref ->
            pref[NOMBRE] = nombre
            pref[CORREO] = correo
            pref[ROL] = rol
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { it.clear() }
    }
}