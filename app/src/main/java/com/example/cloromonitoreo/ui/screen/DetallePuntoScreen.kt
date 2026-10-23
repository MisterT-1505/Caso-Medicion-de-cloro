package com.example.cloromonitoreo.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cloromonitoreo.ui.viewmodel.MonitoreoViewModel

@Composable
fun DetallePuntoScreen(
    viewModel: MonitoreoViewModel,
    onVolver: () -> Unit,
    onRegistrarAccion: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val mediciones by viewModel.mediciones.collectAsStateWithLifecycle()

    val puntoId = uiState.puntoSeleccionadoId ?: "G1-P1"
    val actual = mediciones.find { it.puntoId == puntoId }

    var nuevoValorPpm by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Detalle del Punto: $puntoId",
            style = MaterialTheme.typography.headlineSmall
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Granja: ${actual?.granja ?: uiState.granjaSeleccionada}")
                Text(text = "Galpón: ${actual?.galpon ?: "Galpón 1"}")
                Text(text = "Ubicación: Extremo línea de bebederos")
                Text(
                    text = "Nivel actual: ${actual?.nivelCloroPpm ?: 0.0} ppm",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = "Estado: ${actual?.estado ?: "Normal"}")
                Text(text = "Último registro: ${actual?.fechaHora ?: "--:--"}")
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Simulación de medición en terreno",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = nuevoValorPpm,
                    onValueChange = { nuevoValorPpm = it },
                    label = { Text("Nuevo nivel de cloro (ppm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val valor = nuevoValorPpm.replace(',', '.').toDoubleOrNull()
                        if (valor != null) {
                            viewModel.registrarNuevaMedicion(
                                puntoId = puntoId,
                                granja = actual?.granja ?: uiState.granjaSeleccionada,
                                galpon = actual?.galpon ?: "Galpón 1",
                                nivelPpm = valor
                            )
                            nuevoValorPpm = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar Medición en Room")
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onVolver, modifier = Modifier.weight(1f)) {
                Text("Volver")
            }
            Button(
                onClick = { onRegistrarAccion(puntoId) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Registrar Acción")
            }
        }
    }
}