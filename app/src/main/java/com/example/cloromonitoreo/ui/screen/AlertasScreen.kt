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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cloromonitoreo.data.model.CloroEstado
import com.example.cloromonitoreo.ui.viewmodel.AlertasViewModel

@Composable
fun AlertasScreen(
    viewModel: AlertasViewModel,
    onResolverAlerta: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val alertas by viewModel.alertasActivas.collectAsStateWithLifecycle()

    val alertasFiltradas = if (uiState.soloCriticas) {
        alertas.filter { it.nivelGravedad == CloroEstado.CRITICO }
    } else {
        alertas
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Alertas y Desviaciones",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))

        FilterChip(
            selected = uiState.soloCriticas,
            onClick = viewModel::toggleFiltroCriticas,
            label = { Text("Mostrar únicamente críticas (<2 o >6 ppm)") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (alertasFiltradas.isEmpty()) {
            Text(
                text = "No hay desviaciones activas en los bebederos.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 24.dp)
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(alertasFiltradas, key = { it.id }) { alerta ->
                    val colorFondo = if (alerta.nivelGravedad == CloroEstado.CRITICO) {
                        Color(0xFFF8D7DA)
                    } else {
                        Color(0xFFFFF3CD)
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = colorFondo)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${alerta.granja} - ${alerta.galpon}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                            }
                            Text(text = "Punto: ${alerta.puntoId}")
                            Text(text = "Desviación: ${alerta.tipoDesviacion.descripcion}")
                            Text(
                                text = "Nivel detectado: ${alerta.nivelPpm} ppm",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(text = "Hora: ${alerta.fechaHora}")

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { onResolverAlerta(alerta.puntoId) },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Aplicar Corrección")
                            }
                        }
                    }
                }
            }
        }
    }
}