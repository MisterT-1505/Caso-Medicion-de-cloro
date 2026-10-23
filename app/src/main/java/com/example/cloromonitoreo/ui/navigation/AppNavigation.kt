package com.example.cloromonitoreo.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cloromonitoreo.ui.screen.AlertasScreen
import com.example.cloromonitoreo.ui.screen.DetallePuntoScreen
import com.example.cloromonitoreo.ui.screen.HistorialScreen
import com.example.cloromonitoreo.ui.screen.HomeScreen
import com.example.cloromonitoreo.ui.screen.LoginScreen
import com.example.cloromonitoreo.ui.screen.PuntosMedicionScreen
import com.example.cloromonitoreo.ui.screen.RegistroAccionScreen
import com.example.cloromonitoreo.ui.viewmodel.AlertasViewModel
import com.example.cloromonitoreo.ui.viewmodel.AuthViewModel
import com.example.cloromonitoreo.ui.viewmodel.MonitoreoViewModel
import com.example.cloromonitoreo.ui.viewmodel.RegistroAccionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    monitoreoViewModel: MonitoreoViewModel,
    alertasViewModel: AlertasViewModel,
    registroAccionViewModel: RegistroAccionViewModel
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: AppRoutes.LOGIN

    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val nombreUsuario = authState.usuarioAutenticado?.nombre ?: "Usuario"
    val rolUsuario = authState.usuarioAutenticado?.rol?.name ?: "OPERARIO"

    val mostrarBarras = currentRoute != AppRoutes.LOGIN

    val tituloTopBar = when (currentRoute) {
        AppRoutes.HOME -> "Panel de Control"
        AppRoutes.PUNTOS_MEDICION -> "Puntos de Medición"
        AppRoutes.DETALLE_PUNTO -> "Detalle de Punto"
        AppRoutes.ALERTAS -> "Desviaciones y Alertas"
        AppRoutes.REGISTRO_ACCION -> "Acción Correctiva"
        AppRoutes.HISTORIAL -> "Historial de Registros"
        else -> "Monitoreo de Cloro"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = mostrarBarras,
        drawerContent = {
            if (mostrarBarras) {
                ModalDrawerSheet {
                    Text(
                        text = "Sistema Cloro Línea",
                        modifier = Modifier.padding(24.dp)
                    )
                    NavigationDrawerItem(
                        selected = currentRoute == AppRoutes.HOME,
                        onClick = {
                            navController.navigate(AppRoutes.HOME)
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Inicio") }
                    )
                    NavigationDrawerItem(
                        selected = currentRoute == AppRoutes.PUNTOS_MEDICION,
                        onClick = {
                            navController.navigate(AppRoutes.PUNTOS_MEDICION)
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.List, contentDescription = null) },
                        label = { Text("Puntos de Medición") }
                    )
                    NavigationDrawerItem(
                        selected = currentRoute == AppRoutes.ALERTAS,
                        onClick = {
                            navController.navigate(AppRoutes.ALERTAS)
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                        label = { Text("Alertas") }
                    )
                    NavigationDrawerItem(
                        selected = currentRoute == AppRoutes.HISTORIAL,
                        onClick = {
                            navController.navigate(AppRoutes.HISTORIAL)
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.History, contentDescription = null) },
                        label = { Text("Historial") }
                    )
                    NavigationDrawerItem(
                        selected = false,
                        onClick = {
                            authViewModel.cerrarSesion {
                                navController.navigate(AppRoutes.LOGIN) {
                                    popUpTo(0)
                                }
                            }
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                        label = { Text("Cerrar Sesión") }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (mostrarBarras) {
                    TopAppBar(
                        title = { Text(tituloTopBar) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menú")
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (mostrarBarras) {
                    NavigationBar {
                        NavigationBarItem(
                            selected = currentRoute == AppRoutes.HOME,
                            onClick = { navController.navigate(AppRoutes.HOME) },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                            label = { Text("Inicio") }
                        )
                        NavigationBarItem(
                            selected = currentRoute == AppRoutes.PUNTOS_MEDICION,
                            onClick = { navController.navigate(AppRoutes.PUNTOS_MEDICION) },
                            icon = { Icon(Icons.Default.List, contentDescription = "Puntos") },
                            label = { Text("Puntos") }
                        )
                        NavigationBarItem(
                            selected = currentRoute == AppRoutes.ALERTAS,
                            onClick = { navController.navigate(AppRoutes.ALERTAS) },
                            icon = { Icon(Icons.Default.Warning, contentDescription = "Alertas") },
                            label = { Text("Alertas") }
                        )
                        NavigationBarItem(
                            selected = currentRoute == AppRoutes.HISTORIAL,
                            onClick = { navController.navigate(AppRoutes.HISTORIAL) },
                            icon = { Icon(Icons.Default.History, contentDescription = "Historial") },
                            label = { Text("Historial") }
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = AppRoutes.LOGIN,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(AppRoutes.LOGIN) {
                    LoginScreen(
                        viewModel = authViewModel,
                        onLoginSuccess = {
                            navController.navigate(AppRoutes.HOME) {
                                popUpTo(AppRoutes.LOGIN) { inclusive = true }
                            }
                        }
                    )
                }
                composable(AppRoutes.HOME) {
                    HomeScreen(
                        viewModel = monitoreoViewModel,
                        usuarioNombre = nombreUsuario,
                        usuarioRol = rolUsuario
                    )
                }
                composable(AppRoutes.PUNTOS_MEDICION) {
                    PuntosMedicionScreen(
                        viewModel = monitoreoViewModel,
                        onVerDetalle = {
                            navController.navigate(AppRoutes.DETALLE_PUNTO)
                        }
                    )
                }
                composable(AppRoutes.DETALLE_PUNTO) {
                    DetallePuntoScreen(
                        viewModel = monitoreoViewModel,
                        onVolver = { navController.popBackStack() },
                        onRegistrarAccion = { puntoId ->
                            registroAccionViewModel.inicializarFormulario(
                                puntoId = puntoId,
                                granja = monitoreoViewModel.uiState.value.granjaSeleccionada,
                                galpon = "Galpón 1",
                                responsable = nombreUsuario
                            )
                            navController.navigate(AppRoutes.REGISTRO_ACCION)
                        }
                    )
                }
                composable(AppRoutes.ALERTAS) {
                    AlertasScreen(
                        viewModel = alertasViewModel,
                        onResolverAlerta = { puntoId ->
                            registroAccionViewModel.inicializarFormulario(
                                puntoId = puntoId,
                                granja = "Granja Melipilla",
                                galpon = "Galpón 1",
                                responsable = nombreUsuario
                            )
                            navController.navigate(AppRoutes.REGISTRO_ACCION)
                        }
                    )
                }
                composable(AppRoutes.REGISTRO_ACCION) {
                    RegistroAccionScreen(
                        viewModel = registroAccionViewModel,
                        onGuardadoExitoso = {
                            navController.popBackStack()
                        },
                        onCancelar = { navController.popBackStack() }
                    )
                }
                composable(AppRoutes.HISTORIAL) {
                    HistorialScreen(viewModel = monitoreoViewModel)
                }
            }
        }
    }
}