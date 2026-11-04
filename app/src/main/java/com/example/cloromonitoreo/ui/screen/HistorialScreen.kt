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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.cloromonitoreo.data.local.entity.AccionCorrectivaEntity
import com.example.cloromonitoreo.data.local.entity.MedicionEntity
import com.example.cloromonitoreo.ui.viewmodel.MonitoreoViewModel
import java.util.Locale

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HistorialScreen(
    viewModel: MonitoreoViewModel,
    usuarioRol: String = "OPERARIO"
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val mediciones by viewModel.mediciones.collectAsStateWithLifecycle()
    val acciones by viewModel.accionesCorrectivas.collectAsStateWithLifecycle()
    var verAcciones by remember { mutableStateOf(false) }

    val periodos = listOf("24 horas", "7 días", "30 días")

    val medicionesPorPeriodo = when (uiState.periodoHistorial) {
        "24 horas" -> mediciones.take(12)
        "7 días" -> mediciones.take(30)
        else -> mediciones
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth < 600.dp) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Historial en SQLite",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Período activo: Últimas ${uiState.periodoHistorial} | Rol: $usuarioRol",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                FiltrosHistorial(
                    verAcciones = verAcciones,
                    onToggleVista = { verAcciones = !verAcciones },
                    periodos = periodos,
                    periodoSeleccionado = uiState.periodoHistorial,
                    onSeleccionarPeriodo = viewModel::seleccionarPeriodoHistorial
                )
                Spacer(modifier = Modifier.height(12.dp))
                ContenidoHistorial(
                    verAcciones = verAcciones,
                    mediciones = medicionesPorPeriodo,
                    acciones = acciones,
                    usuarioRol = usuarioRol,
                    onValidarAccion = viewModel::validarAccionPorSupervisor,
                    onEliminarAccion = viewModel::eliminarAccionCorrectiva
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Historial en SQLite",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Consulta de trazabilidad, evidencias fotográficas y validación de cierres:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FiltrosHistorial(
                        verAcciones = verAcciones,
                        onToggleVista = { verAcciones = !verAcciones },
                        periodos = periodos,
                        periodoSeleccionado = uiState.periodoHistorial,
                        onSeleccionarPeriodo = viewModel::seleccionarPeriodoHistorial
                    )
                }
                Column(
                    modifier = Modifier.weight(1.5f)
                ) {
                    ContenidoHistorial(
                        verAcciones = verAcciones,
                        mediciones = medicionesPorPeriodo,
                        acciones = acciones,
                        usuarioRol = usuarioRol,
                        onValidarAccion = viewModel::validarAccionPorSupervisor,
                        onEliminarAccion = viewModel::eliminarAccionCorrectiva
                    )
                }
            }
        }
    }
}

@Composable
private fun FiltrosHistorial(
    verAcciones: Boolean,
    onToggleVista: () -> Unit,
    periodos: List<String>,
    periodoSeleccionado: String,
    onSeleccionarPeriodo: (String) -> Unit
) {
    FilterChip(
        selected = verAcciones,
        onClick = onToggleVista,
        label = { Text(if (verAcciones) "Viendo: Acciones Correctivas" else "Viendo: Mediciones") }
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(periodos) { periodo ->
            FilterChip(
                selected = periodoSeleccionado == periodo,
                onClick = { onSeleccionarPeriodo(periodo) },
                label = { Text("Últimas $periodo") }
            )
        }
    }
}

@Composable
private fun ContenidoHistorial(
    verAcciones: Boolean,
    mediciones: List<MedicionEntity>,
    acciones: List<AccionCorrectivaEntity>,
    usuarioRol: String,
    onValidarAccion: (AccionCorrectivaEntity) -> Unit,
    onEliminarAccion: (AccionCorrectivaEntity) -> Unit
) {
    if (verAcciones) {
        if (acciones.isEmpty()) {
            Text(
                text = "No hay acciones correctivas registradas aún.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(acciones, key = { it.id }) { accion ->
                    val colorTarjeta = if (accion.validadaPorSupervisor) {
                        Color(0xFFD4EDDA)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = colorTarjeta)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Intervención en Punto: ${accion.puntoId}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(text = "Responsable: ${accion.responsable}")
                            Text(text = "Descripción: ${accion.descripcion}")
                            Text(text = "Nuevo cloro: ${String.format(Locale.US, "%.2f", accion.nuevoNivelPpm)} ppm")
                            Text(
                                text = if (accion.validadaPorSupervisor) {
                                    "Estado: Validada y cerrada por Supervisor"
                                } else {
                                    "Estado: Pendiente de validación del Supervisor"
                                },
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(text = "Fecha: ${accion.fechaHora}", style = MaterialTheme.typography.bodySmall)

                            if (!accion.imagenUri.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Evidencia fotográfica adjunta:",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                AsyncImage(
                                    model = accion.imagenUri,
                                    contentDescription = "Foto de evidencia",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(240.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            if (usuarioRol == "SUPERVISOR" || usuarioRol == "JEFATURA") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (!accion.validadaPorSupervisor) {
                                        Button(onClick = { onValidarAccion(accion) }) {
                                            Text("Validar Cierre")
                                        }
                                    }
                                    OutlinedButton(onClick = { onEliminarAccion(accion) }) {
                                        Text("Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(mediciones, key = { it.id }) { reg ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "${reg.granja} (${reg.galpon})", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "${String.format(Locale.US, "%.2f", reg.nivelCloroPpm)} ppm",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Text(text = "Punto: ${reg.puntoId} | Estado: ${reg.estado}")
                        Text(text = "Fecha/Hora: ${reg.fechaHora}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}