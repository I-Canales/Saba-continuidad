package com.example.sabacontinuidad.navigation

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
import androidx.compose.runtime.key
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sabacontinuidad.ui.ayuda.AyudaAccesoScreen
import com.example.sabacontinuidad.ui.escaner.EscanerQrScreen
import com.example.sabacontinuidad.ui.inicio.InicioScreen
import com.example.sabacontinuidad.ui.login.LoginScreen
import com.example.sabacontinuidad.ui.reporte.ReporteScreen
import com.example.sabacontinuidad.ui.reporteguiado.ProblemaScreen
import com.example.sabacontinuidad.ui.reporteguiado.RevisionReporteScreen
import com.example.sabacontinuidad.ui.reporteguiado.SeleccionEquipoScreen
import com.example.sabacontinuidad.viewmodel.AuthViewModel
import com.example.sabacontinuidad.viewmodel.IncidenteViewModel

object Rutas {
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val AYUDA = "ayuda"
    const val EQUIPO = "equipo"
    const val PROBLEMA = "problema"
    const val REVISION = "revision"
    const val REPORTE = "reporte"
    const val ESCANER = "escaner"
}

@Composable
fun AppNavigation(
    authVm: AuthViewModel,
    incidenteVm: IncidenteViewModel = viewModel()
) {
    val autenticacion by authVm.estado.collectAsState()
    val formulario by incidenteVm.form.collectAsState()

    val usuario = autenticacion.usuario

    // Limpia el formulario cuando cambia la sesión.
    LaunchedEffect(usuario) {
        incidenteVm.limpiar()
    }

    // Muestra los errores de autenticación.
    autenticacion.mensaje?.let { mensaje ->
        AlertDialog(
            onDismissRequest = {
                authVm.cerrarMensaje()
            },
            title = {
                Text("Acceso a Saba")
            },
            text = {
                Text(mensaje)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        authVm.cerrarMensaje()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Reinicia la navegación al iniciar o cerrar sesión.
    key(usuario) {
        val nav = rememberNavController()

        NavHost(
            navController = nav,
            startDestination = if (usuario == null) {
                Rutas.LOGIN
            } else {
                Rutas.INICIO
            },
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it / 4 }
                ) + fadeIn()
            },
            exitTransition = {
                fadeOut()
            },
            popEnterTransition = {
                fadeIn()
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it / 4 }
                ) + fadeOut()
            }
        ) {
            if (usuario == null) {

                // LOGIN
                composable(Rutas.LOGIN) {
                    LoginScreen(
                        cargando = autenticacion.cargando,
                        alPresionarMicrosoft = {
                            authVm.iniciarSesion()
                        },
                        alPresionarAyuda = {
                            nav.navigate(Rutas.AYUDA) {
                                launchSingleTop = true
                            }
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

            } else {

                // INICIO
                composable(Rutas.INICIO) {
                    InicioScreen(
                        usuario = usuario,

                        // Pendiente: conectar la lista de incidentes.
                        incidentes = emptyList(),
                        errorCarga = null,
                        aviso = null,
                        alCerrarAviso = {},

                        alReportar = {
                            nav.navigate(Rutas.EQUIPO) {
                                launchSingleTop = true
                            }
                        },

                        alCerrarSesion = {
                            authVm.cerrarSesion()
                        }
                    )
                }

                // PASO 1: SELECCIONAR EQUIPO
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

                // PASO 2: DESCRIBIR EL PROBLEMA
                composable(Rutas.PROBLEMA) {
                    ProblemaScreen(
                        vm = incidenteVm,

                        alCambiarEquipo = {
                            nav.popBackStack()
                        },

                        alContinuar = {
                            nav.navigate(Rutas.REVISION) {
                                launchSingleTop = true
                            }
                        },

                        alVolver = {
                            nav.popBackStack()
                        }
                    )
                }

                // PASO 3: REVISAR Y ENVIAR
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
                            // Pendiente: conectar el contacto de soporte.
                        }
                    )
                }

                // FORMULARIO DE REPORTE ORIGINAL
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
            }
        }
    }
}