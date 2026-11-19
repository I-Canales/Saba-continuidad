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
import com.example.sabacontinuidad.ui.reporte.ProblemaScreen
import com.example.sabacontinuidad.ui.reporte.RevisionReporteScreen
import com.example.sabacontinuidad.ui.reporte.SeleccionEquipoScreen
import com.example.sabacontinuidad.ui.incidentes.IncidentDetailScreen
import com.example.sabacontinuidad.ui.incidentes.SupportRequestScreen
import com.example.sabacontinuidad.ui.incidentes.RegisterManagementScreen
import com.example.sabacontinuidad.viewmodel.AuthViewModel
import com.example.sabacontinuidad.viewmodel.IncidenteViewModel

object Rutas {
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val AYUDA = "ayuda"
    const val EQUIPO = "equipo"
    const val PROBLEMA = "problema"
    const val REVISION = "revision"
    const val DETALLE_INCIDENTE = "detalle_incidente"
    const val SOLICITAR_SOPORTE = "solicitar_soporte"
    const val REGISTRAR_GESTION = "registrar_gestion
    const val ESCANER = "escaner"
}

@Composable
fun AppNavigation(
    authVm: AuthViewModel = viewModel(),
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

                // S01: inicio de sesión con Microsoft.
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

                // Ayuda para acceder.
                composable(Rutas.AYUDA) {
                    AyudaAccesoScreen(
                        alVolver = {
                            nav.popBackStack()
                        }
                    )
                }

            } else {

                // S02: pantalla de inicio.
                composable(Rutas.INICIO) {
                    InicioScreen(
                        usuario = usuario,
                        incidentes = emptyList(),
                        errorCarga = null,
                        aviso = null,
                        alCerrarAviso = {},
                        alReportar = {
                            nav.navigate(Rutas.EQUIPO) {
                                launchSingleTop = true
                            }
                        },
                        alVerIncidentes = {
                            nav.navigate(Rutas.DETALLE_INCIDENTE) {
                                launchSingleTop = true
                            }
                        },
                        alCerrarSesion = {
                            authVm.cerrarSesion()
                        }
                    )
                }

                // S04: seleccionar equipo.
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

                // S05: describir el problema.
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

                // S06: revisar y enviar el reporte.
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
                            // Pendiente: conectar contacto de soporte.
                        }
                    )
                }
                
        // S07 - DETALLE DEL INCIDENTE
        composable(Rutas.DETALLE_INCIDENTE) {
            IncidentDetailScreen(
                onBack = {
                    nav.popBackStack()
                },
                onRequestSupport = {
                    nav.navigate(Rutas.SOLICITAR_SOPORTE)
                },
                onRegisterAction = {
                    nav.navigate(Rutas.REGISTRAR_GESTION)
                },
                onScanQr = {
                    nav.navigate(Rutas.ESCANER)
                },
                onNavSelect = { selected ->
                    if (selected == 0) {
                        nav.navigate(Rutas.INICIO) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        // S08 - SOLICITAR SOPORTE
        composable(Rutas.SOLICITAR_SOPORTE) {
            SupportRequestScreen(
                onBack = {
                    nav.popBackStack()
                },
                onOpenIncident = {
                    nav.popBackStack()
                },
                onRegisterManagement = {
                    nav.navigate(Rutas.REGISTRAR_GESTION)
                }
            )
        }

        // S09 - REGISTRAR GESTIÓN
        composable(Rutas.REGISTRAR_GESTION) {
            RegisterManagementScreen(
                onBack = {
                    nav.popBackStack()
                },
                onSave = { gestion ->
                    // Pendiente: guardar la gestión
                    // en Room y Supabase.
                }
            )
        }


                // Escáner de códigos QR.
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
