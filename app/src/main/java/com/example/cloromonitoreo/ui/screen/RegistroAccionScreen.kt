package com.example.cloromonitoreo.ui.screen

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.cloromonitoreo.ui.viewmodel.RegistroAccionViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
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

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        if (maxWidth < 600.dp) {
            // MODO VERTICAL / COMPACTO (< 600dp)
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

                TarjetaDetalleAlerta(state.puntoId, state.granja, state.galpon)

                FormularioCampos(
                    responsable = state.responsable,
                    onResponsableChange = viewModel::onResponsableChange,
                    errorResponsable = state.errorResponsable,
                    descripcion = state.descripcion,
                    onDescripcionChange = viewModel::onDescripcionChange,
                    errorDescripcion = state.errorDescripcion,
                    nuevoPpm = state.nuevoPpm,
                    onNuevoPpmChange = viewModel::onNuevoPpmChange,
                    errorNuevoPpm = state.errorNuevoPpm
                )

                SeccionEvidenciaFotografica(
                    state = state,
                    onAbrirCamara = { launcherCamara.launch(null) },
                    onAbrirGaleria = { launcherGaleria.launch("image/*") },
                    onLimpiarFoto = viewModel::limpiarFoto
                )

                Spacer(modifier = Modifier.height(6.dp))

                BotonesAccion(
                    onGuardar = { viewModel.guardarAccionCorrectiva(onGuardadoExitoso) },
                    onCancelar = onCancelar
                )
            }
        } else {
            // MODO HORIZONTAL / EXPANDIDO (>= 600dp): Dos columnas balanceadas
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Columna Izquierda: Información y formulario de datos
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Registro de Acción Correctiva",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    TarjetaDetalleAlerta(state.puntoId, state.granja, state.galpon)

                    FormularioCampos(
                        responsable = state.responsable,
                        onResponsableChange = viewModel::onResponsableChange,
                        errorResponsable = state.errorResponsable,
                        descripcion = state.descripcion,
                        onDescripcionChange = viewModel::onDescripcionChange,
                        errorDescripcion = state.errorDescripcion,
                        nuevoPpm = state.nuevoPpm,
                        onNuevoPpmChange = viewModel::onNuevoPpmChange,
                        errorNuevoPpm = state.errorNuevoPpm
                    )

                    BotonesAccion(
                        onGuardar = { viewModel.guardarAccionCorrectiva(onGuardadoExitoso) },
                        onCancelar = onCancelar
                    )
                }

                // Columna Derecha: Gestión de evidencia fotográfica (Cámara / Galería)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SeccionEvidenciaFotografica(
                        state = state,
                        onAbrirCamara = { launcherCamara.launch(null) },
                        onAbrirGaleria = { launcherGaleria.launch("image/*") },
                        onLimpiarFoto = viewModel::limpiarFoto
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaDetalleAlerta(puntoId: String, granja: String, galpon: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Punto intervenido: $puntoId", style = MaterialTheme.typography.titleMedium)
            Text(text = "Ubicación: $granja - $galpon", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun FormularioCampos(
    responsable: String,
    onResponsableChange: (String) -> Unit,
    errorResponsable: String?,
    descripcion: String,
    onDescripcionChange: (String) -> Unit,
    errorDescripcion: String?,
    nuevoPpm: String,
    onNuevoPpmChange: (String) -> Unit,
    errorNuevoPpm: String?
) {
    OutlinedTextField(
        value = responsable,
        onValueChange = onResponsableChange,
        label = { Text("Responsable de la acción") },
        isError = errorResponsable != null,
        supportingText = { errorResponsable?.let { Text(it) } },
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = descripcion,
        onValueChange = onDescripcionChange,
        label = { Text("Descripción de la corrección realizada") },
        isError = errorDescripcion != null,
        supportingText = { errorDescripcion?.let { Text(it) } },
        minLines = 3,
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = nuevoPpm,
        onValueChange = onNuevoPpmChange,
        label = { Text("Nuevo nivel de cloro medido (ppm)") },
        placeholder = { Text("Ej: 4.20") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        isError = errorNuevoPpm != null,
        supportingText = { errorNuevoPpm?.let { Text(it) } },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SeccionEvidenciaFotografica(
    state: com.example.cloromonitoreo.ui.viewmodel.RegistroAccionUiState,
    onAbrirCamara: () -> Unit,
    onAbrirGaleria: () -> Unit,
    onLimpiarFoto: () -> Unit
) {
    Text(
        text = "Respaldo Fotográfico de la Intervención",
        style = MaterialTheme.typography.titleMedium
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onAbrirCamara,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text("Cámara")
        }

        OutlinedButton(
            onClick = onAbrirGaleria,
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
                bitmap = state.bitmapFoto.asImageBitmap(),
                contentDescription = "Foto capturada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "Foto obtenida desde: Cámara",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            OutlinedButton(onClick = onLimpiarFoto) {
                Text("Eliminar fotografía")
            }
        }
        state.uriFoto != null -> {
            AsyncImage(
                model = state.uriFoto,
                contentDescription = "Imagen seleccionada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "Foto obtenida desde: Galería",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            OutlinedButton(onClick = onLimpiarFoto) {
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
}

@Composable
private fun BotonesAccion(
    onGuardar: () -> Unit,
    onCancelar: () -> Unit
) {
    Button(
        onClick = onGuardar,
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