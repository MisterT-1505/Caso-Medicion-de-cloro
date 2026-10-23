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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cloromonitoreo.data.model.RolUsuario
import com.example.cloromonitoreo.ui.viewmodel.AuthViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (RolUsuario) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth < 600.dp) {
            // MODO COMPACTO / VERTICAL
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                EncabezadoLogin()
                Spacer(modifier = Modifier.height(24.dp))
                FormularioLogin(
                    state = state,
                    viewModel = viewModel,
                    onLoginSuccess = onLoginSuccess
                )
                Spacer(modifier = Modifier.height(20.dp))
                TarjetaCuentasDemo(viewModel = viewModel)
            }
        } else {
            // MODO EXPANDIDO / HORIZONTAL (2 Columnas)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    EncabezadoLogin()
                    Spacer(modifier = Modifier.height(24.dp))
                    TarjetaCuentasDemo(viewModel = viewModel)
                }

                Column(
                    modifier = Modifier.weight(1.2f),
                    verticalArrangement = Arrangement.Center
                ) {
                    FormularioLogin(
                        state = state,
                        viewModel = viewModel,
                        onLoginSuccess = onLoginSuccess
                    )
                }
            }
        }
    }
}

@Composable
private fun EncabezadoLogin() {
    Icon(
        imageVector = Icons.Default.WaterDrop,
        contentDescription = "Logo Cloro",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.height(64.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = "Monitoreo de Cloro",
        style = MaterialTheme.typography.headlineMedium
    )
    Text(
        text = "Control Sanitario en Línea",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun FormularioLogin(
    state: com.example.cloromonitoreo.ui.viewmodel.AuthUiState,
    viewModel: AuthViewModel,
    onLoginSuccess: (RolUsuario) -> Unit
) {
    OutlinedTextField(
        value = state.correo,
        onValueChange = viewModel::onCorreoChange,
        label = { Text("Correo Electrónico") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        isError = state.errorCorreo != null,
        supportingText = { state.errorCorreo?.let { Text(it) } },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = state.contrasena,
        onValueChange = viewModel::onContrasenaChange,
        label = { Text("Contraseña") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        isError = state.errorContrasena != null,
        supportingText = { state.errorContrasena?.let { Text(it) } },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = { viewModel.iniciarSesion(onLoginSuccess) },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text("Ingresar")
    }
}

@Composable
private fun TarjetaCuentasDemo(viewModel: AuthViewModel) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Cuentas de demostración (MVP):",
                style = MaterialTheme.typography.labelMedium
            )
            TextButton(onClick = {
                viewModel.onCorreoChange("operario@empresa.cl")
                viewModel.onContrasenaChange("1234")
            }) {
                Text("Operario: operario@empresa.cl")
            }
            TextButton(onClick = {
                viewModel.onCorreoChange("supervisor@empresa.cl")
                viewModel.onContrasenaChange("1234")
            }) {
                Text("Supervisor: supervisor@empresa.cl")
            }
            TextButton(onClick = {
                viewModel.onCorreoChange("jefatura@empresa.cl")
                viewModel.onContrasenaChange("1234")
            }) {
                Text("Jefatura: jefatura@empresa.cl")
            }
        }
    }
}