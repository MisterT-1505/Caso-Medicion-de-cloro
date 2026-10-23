package com.example.cloromonitoreo.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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

@SuppressLint("UnusedBoxWithConstraintsScope")
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

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth < 600.dp) {
            // MODO COMPACTO / VERTICAL
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Detalle del Punto: $puntoId",
                    style = MaterialTheme.typography.headlineSmall
                )

                TarjetaInfoPunto(actual = actual, granjaFallback = uiState.granjaSeleccionada)

                TarjetaSimulacionPunto(
                    nuevoValorPpm = nuevoValorPpm,
                    onValorChange = { nuevoValorPpm = it },
                    onRegistrar = {
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
                    }
                )

                BotonesDetalle(
                    onVolver = onVolver,
                    onRegistrarAccion = { onRegistrarAccion(puntoId) }
                )
            }
        } else {
            // MODO EXPANDIDO / HORIZONTAL (2 Columnas)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Detalle del Punto: $puntoId",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    TarjetaInfoPunto(actual = actual, granjaFallback = uiState.granjaSeleccionada)
                    BotonesDetalle(
                        onVolver = onVolver,
                        onRegistrarAccion = { onRegistrarAccion(puntoId) }
                    )
                }

                Column(
                    modifier = Modifier.weight(1.2f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TarjetaSimulacionPunto(
                        nuevoValorPpm = nuevoValorPpm,
                        onValorChange = { nuevoValorPpm = it },
                        onRegistrar = {
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
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaInfoPunto(
    actual: com.example.cloromonitoreo.data.local.entity.MedicionEntity?,
    granjaFallback: String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Granja: ${actual?.granja ?: granjaFallback}")
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
}

@Composable
private fun TarjetaSimulacionPunto(
    nuevoValorPpm: String,
    onValorChange: (String) -> Unit,
    onRegistrar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Simulación de medición en terreno",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = nuevoValorPpm,
                onValueChange = onValorChange,
                label = { Text("Nuevo nivel de cloro (ppm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onRegistrar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar Medición en Room")
            }
        }
    }
}

@Composable
private fun BotonesDetalle(
    onVolver: () -> Unit,
    onRegistrarAccion: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(onClick = onVolver, modifier = Modifier.weight(1f)) {
            Text("Volver")
        }
        Button(onClick = onRegistrarAccion, modifier = Modifier.weight(1f)) {
            Text("Registrar Acción")
        }
    }
}