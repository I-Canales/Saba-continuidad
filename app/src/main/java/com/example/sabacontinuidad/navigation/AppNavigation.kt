package com.example.sabacontinuidad.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sabacontinuidad.ui.reporte.ProblemaScreen
import com.example.sabacontinuidad.ui.ayuda.AyudaAccesoScreen
import com.example.sabacontinuidad.ui.escaner.EscanerQrScreen
import com.example.sabacontinuidad.ui.inicio.InicioScreen
import com.example.sabacontinuidad.ui.login.LoginScreen
import com.example.sabacontinuidad.ui.reporte.ReporteScreen
import com.example.sabacontinuidad.ui.reporte.SeleccionEquipoScreen
import com.example.sabacontinuidad.ui.reporte.RevisionReporteScreen
import com.example.sabacontinuidad.viewmodel.IncidenteViewModel

object Rutas {
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val AYUDA = "ayuda"
    const val EQUIPO = "equipo"
    const val REPORTE = "reporte"
    const val ESCANER = "escaner"
    const val PROBLEMA = "problema"
    const val REVISION = "revision"
}

@Composable
fun AppNavigation(
    incidenteVm: IncidenteViewModel = viewModel()
) {
    val nav = rememberNavController()

    // Conserva el equipo seleccionado entre pantallas.
    val formulario by incidenteVm.form.collectAsState()

    NavHost(
        navController = nav,
        startDestination = Rutas.LOGIN,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it / 4 }
            ) + fadeIn()
        },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it / 4 }
            ) + fadeOut()
        }
    ) {

        // S01 - LOGIN
        composable(Rutas.LOGIN) {
            LoginScreen(
                cargando = false,

                // Acceso temporal sin Microsoft.
                alPresionarMicrosoft = {
                    nav.navigate(Rutas.INICIO) {
                        popUpTo(Rutas.LOGIN) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },

                alPresionarAyuda = {
                    nav.navigate(Rutas.AYUDA)
                }
            )
        }

        // S02 - INICIO
        composable(Rutas.INICIO) {
            InicioScreen(
                usuario = "Modo de demostración · Sin Microsoft",

                // No se cargan datos reales sin sesión.
                incidentes = emptyList(),
                errorCarga = null,
                aviso = null,
                alCerrarAviso = {},

                // NUEVO: abre la pantalla S04.
                alReportar = {
                    nav.navigate(Rutas.EQUIPO) {
                        launchSingleTop = true
                    }
                },

                alCerrarSesion = {
                    incidenteVm.limpiar()

                    nav.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.INICIO) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // S04 - SELECCIONAR EQUIPO
        composable(Rutas.EQUIPO) {
            SeleccionEquipoScreen(
                codigoActual = formulario.equipo,

                alElegirEquipo = { codigo ->
                    incidenteVm.onEquipo(codigo)
                },

                alContinuar = {
                    nav.navigate(Rutas.PROBLEMA) {
                        launchSingleTop = true
                    }
                },

                alVolver = {
                    nav.popBackStack()
                }
            )
        }
        composable(Rutas.PROBLEMA) {
            ProblemaScreen(
                vm = incidenteVm,
                alCambiarEquipo = {
                    nav.popBackStack()
                },
                alContinuar = {
                    nav.navigate(Rutas.REVISION)
                },
                alVolver = {
                    nav.popBackStack()
                }
            )
        }

        composable(Rutas.REVISION) {
            RevisionReporteScreen(
                vm = incidenteVm,

                alVolver = {
                    nav.popBackStack()
                },

                alEditar = {
                    nav.popBackStack()
                },

                alEnviado = {
                    nav.navigate(Rutas.INICIO) {
                        popUpTo(Rutas.INICIO) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },

                alLlamarSoporte = {
                    // Pendiente de conectar con soporte.
                }
            )
        }


        // FORMULARIO DE REPORTE EXISTENTE
        composable(Rutas.REPORTE) {
            ReporteScreen(
                vm = incidenteVm,

                alEscanear = {
                    nav.navigate(Rutas.ESCANER) {
                        launchSingleTop = true
                    }
                },

                alVolver = {
                    nav.popBackStack()
                }
            )
        }

        // ESCÁNER QR
        composable(Rutas.ESCANER) {
            EscanerQrScreen(
                alLeerCodigo = { codigo ->
                    incidenteVm.onEquipo(codigo)
                    nav.popBackStack()
                },

                alVolver = {
                    nav.popBackStack()
                }
            )
        }

        // AYUDA DE ACCESO
        composable(Rutas.AYUDA) {
            AyudaAccesoScreen(
                alVolver = {
                    nav.popBackStack()
                }
            )
        }
    }
}
