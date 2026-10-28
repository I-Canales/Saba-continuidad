package com.example.sabacontinuidad.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sabacontinuidad.ui.ayuda.AyudaAccesoScreen
import com.example.sabacontinuidad.ui.inicio.InicioScreen
import com.example.sabacontinuidad.ui.login.LoginScreen
import com.example.sabacontinuidad.ui.reporte.ReporteScreen
import com.example.sabacontinuidad.viewmodel.AuthViewModel
import com.example.sabacontinuidad.viewmodel.IncidenteViewModel

object Rutas {
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val AYUDA = "ayuda"
    const val REPORTE = "reporte"
}

@Composable
fun AppNavigation(
    vm: AuthViewModel = viewModel(),
    incidenteVm: IncidenteViewModel = viewModel()
) {
    val nav = rememberNavController()
    val estado by vm.estado.collectAsState()
    val incidentes by incidenteVm.incidentes.collectAsState()
    val activity = LocalActivity.current

    LaunchedEffect(estado.usuario) {
        if (estado.usuario != null) {
            nav.navigate(Rutas.INICIO) {
                popUpTo(Rutas.LOGIN) { inclusive = true }
            }
        } else if (nav.currentDestination?.route == Rutas.INICIO ||
            nav.currentDestination?.route == Rutas.REPORTE
        ) {
            nav.navigate(Rutas.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = nav,
        startDestination = Rutas.LOGIN,
        enterTransition = { slideInHorizontally(initialOffsetX = { it / 4 }) + fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it / 4 }) + fadeOut() }
    ) {

        composable(Rutas.LOGIN) {
            LoginScreen(
                cargando = estado.cargando,
                alPresionarMicrosoft = { activity?.let { vm.iniciarSesion(it) } },
                alPresionarAyuda = { nav.navigate(Rutas.AYUDA) }
            )
        }

        composable(Rutas.INICIO) {
            InicioScreen(
                usuario = estado.usuario ?: "",
                incidentes = incidentes,
                alReportar = { nav.navigate(Rutas.REPORTE) },
                alCerrarSesion = { vm.cerrarSesion() }
            )
        }

        composable(Rutas.REPORTE) {
            ReporteScreen(
                vm = incidenteVm,
                alVolver = { nav.popBackStack() }
            )
        }

        composable(Rutas.AYUDA) {
            AyudaAccesoScreen(alVolver = { nav.popBackStack() })
        }
    }

    estado.mensaje?.let { mensaje ->
        AlertDialog(
            onDismissRequest = { vm.cerrarMensaje() },
            title = { Text("Acceso") },
            text = { Text(mensaje) },
            confirmButton = {
                TextButton(onClick = { vm.cerrarMensaje() }) {
                    Text("Entendido")
                }
            }
        )
    }
}