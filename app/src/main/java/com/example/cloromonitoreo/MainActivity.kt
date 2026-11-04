package com.example.cloromonitoreo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.cloromonitoreo.data.local.AppDatabase
import com.example.cloromonitoreo.data.repository.CloroApiService
import com.example.cloromonitoreo.data.repository.CloroRepository
import com.example.cloromonitoreo.data.repository.PreferenciasRepository
import com.example.cloromonitoreo.ui.navigation.AppNavigation
import com.example.cloromonitoreo.ui.theme.CloromonitoreoTheme
import com.example.cloromonitoreo.ui.viewmodel.AlertasViewModel
import com.example.cloromonitoreo.ui.viewmodel.AuthViewModel
import com.example.cloromonitoreo.ui.viewmodel.MonitoreoViewModel
import com.example.cloromonitoreo.ui.viewmodel.RegistroAccionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(applicationContext)

        val cloroRepository = CloroRepository(
            medicionDao = database.medicionDao(),
            accionCorrectivaDao = database.accionCorrectivaDao(),
            apiService = CloroApiService()
        )
        val preferenciasRepository = PreferenciasRepository(applicationContext)

        val authViewModel = AuthViewModel(preferenciasRepository)
        val monitoreoViewModel = MonitoreoViewModel(cloroRepository)
        val alertasViewModel = AlertasViewModel(cloroRepository)
        val registroAccionViewModel = RegistroAccionViewModel(cloroRepository)

        setContent {
            CloromonitoreoTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation(
                        authViewModel = authViewModel,
                        monitoreoViewModel = monitoreoViewModel,
                        alertasViewModel = alertasViewModel,
                        registroAccionViewModel = registroAccionViewModel
                    )
                }
            }
        }
    }
}