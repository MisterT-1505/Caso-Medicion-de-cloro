package com.example.cloromonitoreo.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cloromonitoreo.ui.viewmodel.MonitoreoViewModel
import java.util.Locale

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

    // Estadísticas para Jefatura y Supervisión (Preguntas 3 y 28)
    val promedioPpm = if (mediciones.isNotEmpty()) mediciones.map { it.nivelCloroPpm }.average() else 0.0
    val minimoPpm = mediciones.minOfOrNull { it.nivelCloroPpm } ?: 0.0
    val maximoPpm = mediciones.maxOfOrNull { it.nivelCloroPpm } ?: 0.0

    // Estado interactivo y animaciones declarativas (Guía 12)
    var modoEnfoqueCritico by remember { mutableStateOf(false) }

    val colorTarjetaBienvenida by animateColorAsState(
        targetValue = if (modoEnfoqueCritico) {
            if (criticos > 0) Color(0xFFF8D7DA) else Color(0xFFDDF7E8)
        } else {
            MaterialTheme.colorScheme.primaryContainer
        },
        label = "colorTarjetaBienvenida"
    )

    val escalaTitulo by animateFloatAsState(
        targetValue = if (modoEnfoqueCritico) 1.05f else 1f,
        label = "escalaTitulo"
    )

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        if (maxWidth < 600.dp) {
            // PANTALLA COMPACTA / VERTICAL (< 600dp)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TarjetaBienvenidaAnimada(
                    usuarioNombre = usuarioNombre,
                    usuarioRol = usuarioRol,
                    escalaTitulo = escalaTitulo,
                    colorFondo = colorTarjetaBienvenida
                )

                ControlModoEnfoque(
                    activado = modoEnfoqueCritico,
                    onCambio = { modoEnfoqueCritico = it }
                )

                ResumenEstadosRow(
                    normales = normales,
                    advertencias = advertencias,
                    criticos = criticos,
                    destacarCriticos = modoEnfoqueCritico
                )

                TarjetaIndicadoresJefatura(
                    promedio = promedioPpm,
                    minimo = minimoPpm,
                    maximo = maximoPpm
                )

                TarjetaReferenciaTecnica(totalPuntos = totalPuntos)
            }
        } else {
            // PANTALLA GRANDE / HORIZONTAL (>= 600dp)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TarjetaBienvenidaAnimada(
                        usuarioNombre = usuarioNombre,
                        usuarioRol = usuarioRol,
                        escalaTitulo = escalaTitulo,
                        colorFondo = colorTarjetaBienvenida
                    )

                    ControlModoEnfoque(
                        activado = modoEnfoqueCritico,
                        onCambio = { modoEnfoqueCritico = it }
                    )

                    TarjetaReferenciaTecnica(totalPuntos = totalPuntos)
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Estado General de Calidad del Agua",
                        style = MaterialTheme.typography.titleMedium
                    )

                    ResumenEstadosRow(
                        normales = normales,
                        advertencias = advertencias,
                        criticos = criticos,
                        destacarCriticos = modoEnfoqueCritico
                    )

                    TarjetaIndicadoresJefatura(
                        promedio = promedioPpm,
                        minimo = minimoPpm,
                        maximo = maximoPpm
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaBienvenidaAnimada(
    usuarioNombre: String,
    usuarioRol: String,
    escalaTitulo: Float,
    colorFondo: Color
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = colorFondo)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Bienvenido, $usuarioNombre",
                modifier = Modifier.graphicsLayer {
                    scaleX = escalaTitulo
                    scaleY = escalaTitulo
                },
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
}

@Composable
private fun ControlModoEnfoque(
    activado: Boolean,
    onCambio: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Modo supervisión intensiva",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = if (activado) "Destacando estado sanitario activo" else "Vista estándar del panel",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(
                checked = activado,
                onCheckedChange = onCambio
            )
        }
    }
}

@Composable
private fun ResumenEstadosRow(
    normales: Int,
    advertencias: Int,
    criticos: Int,
    destacarCriticos: Boolean
) {
    val escalaCritico by animateFloatAsState(
        targetValue = if (destacarCriticos && criticos > 0) 1.08f else 1f,
        label = "escalaCritico"
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
            modifier = Modifier
                .weight(1f)
                .graphicsLayer {
                    scaleX = escalaCritico
                    scaleY = escalaCritico
                },
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

@Composable
private fun TarjetaIndicadoresJefatura(
    promedio: Double,
    minimo: Double,
    maximo: Double
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Indicadores Estadísticos (Vista Jefatura / Supervisión)",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Promedio", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "${String.format(Locale.US, "%.2f", promedio)} ppm",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Mínimo", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "${String.format(Locale.US, "%.2f", minimo)} ppm",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Máximo", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "${String.format(Locale.US, "%.2f", maximo)} ppm",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaReferenciaTecnica(totalPuntos: Int) {
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
            Text(text = "Registros totales en SQLite: $totalPuntos", style = MaterialTheme.typography.labelSmall)
        }
    }
}