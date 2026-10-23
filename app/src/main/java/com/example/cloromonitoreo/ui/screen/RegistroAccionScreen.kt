package com.example.cloromonitoreo.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.cloromonitoreo.ui.viewmodel.RegistroAccionViewModel

@Composable
fun RegistroAccionScreen(
    viewModel: RegistroAccionViewModel,
    onGuardadoExitoso: () -> Unit,
    onCancelar: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            viewModel.fotoDesdeCamara(bitmap)
        }
    }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.fotoDesdeGaleria(uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Registro de Acción Correctiva",
            style = MaterialTheme.typography.headlineSmall
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Punto afectado: ${state.puntoId}")
                Text(text = "Granja: ${state.granja} - ${state.galpon}")
            }
        }

        OutlinedTextField(
            value = state.responsable,
            onValueChange = viewModel::onResponsableChange,
            label = { Text("Responsable de la acción") },
            isError = state.errorResponsable != null,
            supportingText = { state.errorResponsable?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.descripcion,
            onValueChange = viewModel::onDescripcionChange,
            label = { Text("Descripción de la corrección realizada") },
            isError = state.errorDescripcion != null,
            supportingText = { state.errorDescripcion?.let { Text(it) } },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.nuevoPpm,
            onValueChange = viewModel::onNuevoPpmChange,
            label = { Text("Nuevo nivel de cloro medido (ppm)") },
            placeholder = { Text("Ej: 4.20") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = state.errorNuevoPpm != null,
            supportingText = { state.errorNuevoPpm?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Respaldo Fotográfico de la Intervención",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { launcherCamara.launch(null) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text("Cámara")
            }

            OutlinedButton(
                onClick = { launcherGaleria.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text("Galería")
            }
        }

        when {
            state.bitmapFoto != null -> {
                Image(
                    bitmap = state.bitmapFoto!!.asImageBitmap(),
                    contentDescription = "Foto capturada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "Foto obtenida desde: Cámara",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedButton(onClick = viewModel::limpiarFoto) {
                    Text("Eliminar fotografía")
                }
            }
            state.uriFoto != null -> {
                AsyncImage(
                    model = state.uriFoto,
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "Foto obtenida desde: Galería",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedButton(onClick = viewModel::limpiarFoto) {
                    Text("Eliminar fotografía")
                }
            }
            else -> {
                Text(
                    text = "No se ha adjuntado evidencia fotográfica",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.guardarAccionCorrectiva(onGuardadoExitoso) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Guardar Acción en SQLite")
        }

        OutlinedButton(
            onClick = onCancelar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}