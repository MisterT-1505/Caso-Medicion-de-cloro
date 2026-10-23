package com.example.cloromonitoreo.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cloromonitoreo.ui.viewmodel.RegistroAccionViewModel

@Composable
fun RegistroAccionScreen(
    viewModel: RegistroAccionViewModel,
    onGuardadoExitoso: () -> Unit,
    onCancelar: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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

        Spacer(modifier = Modifier.height(10.dp))

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