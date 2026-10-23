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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.cloromonitoreo.ui.viewmodel.MonitoreoViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HistorialScreen(
    viewModel: MonitoreoViewModel
) {
    val mediciones by viewModel.mediciones.collectAsStateWithLifecycle()
    val acciones by viewModel.accionesCorrectivas.collectAsStateWithLifecycle()

    var verAcciones by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth < 600.dp) {
            // MODO COMPACTO / VERTICAL
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
                    text = "Consulta local de registros y evidencias tomadas en terreno",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                FilterChip(
                    selected = verAcciones,
                    onClick = { verAcciones = !verAcciones },
                    label = { Text(if (verAcciones) "Viendo: Acciones Correctivas" else "Viendo: Mediciones") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ContenidoHistorial(
                    verAcciones = verAcciones,
                    mediciones = mediciones,
                    acciones = acciones
                )
            }
        } else {
            // MODO EXPANDIDO / HORIZONTAL (2 Columnas)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Historial en SQLite",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Consulta local de registros y evidencias fotográficas tomadas en terreno por los operarios:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FilterChip(
                        selected = verAcciones,
                        onClick = { verAcciones = !verAcciones },
                        label = { Text(if (verAcciones) "Viendo: Acciones Correctivas" else "Viendo: Mediciones") }
                    )
                }

                Column(
                    modifier = Modifier.weight(1.5f)
                ) {
                    ContenidoHistorial(
                        verAcciones = verAcciones,
                        mediciones = mediciones,
                        acciones = acciones
                    )
                }
            }
        }
    }
}

@Composable
private fun ContenidoHistorial(
    verAcciones: Boolean,
    mediciones: List<com.example.cloromonitoreo.data.local.entity.MedicionEntity>,
    acciones: List<com.example.cloromonitoreo.data.local.entity.AccionCorrectivaEntity>
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
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Intervención en Punto: ${accion.puntoId}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(text = "Responsable: ${accion.responsable}")
                            Text(text = "Descripción: ${accion.descripcion}")
                            Text(text = "Nuevo cloro: ${accion.nuevoNivelPpm} ppm")
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