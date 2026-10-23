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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cloromonitoreo.ui.viewmodel.MonitoreoViewModel

@Composable
fun PuntosMedicionScreen(
    viewModel: MonitoreoViewModel,
    onVerDetalle: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val mediciones by viewModel.mediciones.collectAsStateWithLifecycle()

    val filtradas = mediciones.filter { it.granja == uiState.granjaSeleccionada }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Puntos de Medición en Línea",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Selector de granjas
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.granjasDisponibles) { granja ->
                FilterChip(
                    selected = uiState.granjaSeleccionada == granja,
                    onClick = { viewModel.seleccionarGranja(granja) },
                    label = { Text(granja) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filtradas, key = { it.id }) { item ->
                val (fondoColor, textoColor) = when (item.estado) {
                    "Normal" -> Pair(Color(0xFFD4EDDA), Color(0xFF155724))
                    "Advertencia" -> Pair(Color(0xFFFFF3CD), Color(0xFF856404))
                    else -> Pair(Color(0xFFF8D7DA), Color(0xFF721C24))
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = fondoColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${item.puntoId} - ${item.galpon}",
                                style = MaterialTheme.typography.titleMedium,
                                color = textoColor
                            )
                            Text(
                                text = "${String.format(java.util.Locale.US, "%.2f", item.nivelCloroPpm)} ppm",
                                style = MaterialTheme.typography.titleLarge,
                                color = textoColor
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Estado: ${item.estado} | Hora: ${item.fechaHora}",
                            style = MaterialTheme.typography.bodySmall,
                            color = textoColor
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                viewModel.seleccionarPunto(item.puntoId)
                                onVerDetalle(item.puntoId)
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Ver Detalle / Simular")
                        }
                    }
                }
            }
        }
    }
}