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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import com.example.cloromonitoreo.ui.viewmodel.MonitoreoViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HomeScreen(
    viewModel: MonitoreoViewModel,
    usuarioNombre: String,
    usuarioRol: String
) {
    val mediciones by viewModel.mediciones.collectAsStateWithLifecycle()

    val totalPuntos = mediciones.size
    val normales = mediciones.count { it.estado == "Normal" }
    val advertencias = mediciones.count { it.estado == "Advertencia" }
    val criticos = mediciones.count { it.estado == "Crítico" }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        if (maxWidth < 600.dp) {
            // PANTALLA COMPACTA / VERTICAL (< 600dp): Dispositivos móviles en terreno
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta Bienvenida
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Bienvenido, $usuarioNombre",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Rol activo: $usuarioRol",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Monitoreo en línea - Red de bebederos avícolas",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Resumen Estados
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD4EDDA))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF155724))
                            Text(text = "$normales", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF155724))
                            Text(text = "Normal", style = MaterialTheme.typography.labelMedium, color = Color(0xFF155724))
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFF856404))
                            Text(text = "$advertencias", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF856404))
                            Text(text = "Advertencia", style = MaterialTheme.typography.labelMedium, color = Color(0xFF856404))
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8D7DA))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFF721C24))
                            Text(text = "$criticos", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF721C24))
                            Text(text = "Crítico", style = MaterialTheme.typography.labelMedium, color = Color(0xFF721C24))
                        }
                    }
                }

                // Referencia técnica
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Referencia de rangos técnicos:",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "• Normal: 3.00 a 5.00 ppm", style = MaterialTheme.typography.bodySmall)
                        Text(text = "• Advertencia: 2.00 a <3.00 ppm o >5.00 a 6.00 ppm", style = MaterialTheme.typography.bodySmall)
                        Text(text = "• Crítico: <2.00 ppm o >6.00 ppm", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Puntos monitoreados en SQLite: $totalPuntos", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        } else {
            // PANTALLA GRANDE / HORIZONTAL (>= 600dp): Tablets o rotación horizontal
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Columna izquierda
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Bienvenido, $usuarioNombre",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Rol activo: $usuarioRol",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Monitoreo en línea - Red de bebederos avícolas",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Referencia de rangos técnicos:",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "• Normal: 3.00 a 5.00 ppm", style = MaterialTheme.typography.bodySmall)
                            Text(text = "• Advertencia: 2.00 a <3.00 ppm o >5.00 a 6.00 ppm", style = MaterialTheme.typography.bodySmall)
                            Text(text = "• Crítico: <2.00 ppm o >6.00 ppm", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Puntos monitoreados en SQLite: $totalPuntos", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                // Columna derecha
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Estado General de Calidad del Agua",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFD4EDDA))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF155724))
                                Text(text = "$normales", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF155724))
                                Text(text = "Normal", style = MaterialTheme.typography.labelMedium, color = Color(0xFF155724))
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFF856404))
                                Text(text = "$advertencias", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF856404))
                                Text(text = "Advertencia", style = MaterialTheme.typography.labelMedium, color = Color(0xFF856404))
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8D7DA))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFF721C24))
                                Text(text = "$criticos", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF721C24))
                                Text(text = "Crítico", style = MaterialTheme.typography.labelMedium, color = Color(0xFF721C24))
                            }
                        }
                    }
                }
            }
        }
    }
}