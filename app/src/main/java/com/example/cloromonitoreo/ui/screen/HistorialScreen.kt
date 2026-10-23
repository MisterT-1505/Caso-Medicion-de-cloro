package com.example.cloromonitoreo.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cloromonitoreo.ui.viewmodel.MonitoreoViewModel

@Composable
fun HistorialScreen(
    viewModel: MonitoreoViewModel
) {
    val mediciones by viewModel.mediciones.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Historial de Mediciones (SQLite)",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Registros almacenados localmente para consulta offline",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(mediciones, key = { it.id }) { reg ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "${reg.granja} (${reg.galpon})", style = MaterialTheme.typography.titleMedium)
                            Text(text = "${reg.nivelCloroPpm} ppm", style = MaterialTheme.typography.titleMedium)
                        }
                        Text(text = "Punto: ${reg.puntoId} | Estado: ${reg.estado}")
                        Text(text = "Fecha/Hora: ${reg.fechaHora}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}