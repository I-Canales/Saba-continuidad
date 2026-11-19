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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sabacontinuidad.ui.ayuda.AyudaAccesoScreen
import com.example.sabacontinuidad.ui.configuraciones.AparienciaScreen
import com.example.sabacontinuidad.ui.configuraciones.ModoTema
import com.example.sabacontinuidad.ui.configuraciones.PerfilScreen
import com.example.sabacontinuidad.ui.escaner.EscanerQrScreen
import com.example.sabacontinuidad.ui.incidentes.IncBottomBar
import com.example.sabacontinuidad.ui.incidentes.IncidentDetailScreen
import com.example.sabacontinuidad.ui.incidentes.RegisterManagementScreen
import com.example.sabacontinuidad.ui.incidentes.SupportRequestScreen
import com.example.sabacontinuidad.ui.inicio.InicioScreen
import com.example.sabacontinuidad.ui.login.LoginScreen
import com.example.sabacontinuidad.ui.reporte.ProblemaScreen
import com.example.sabacontinuidad.ui.reporte.RevisionReporteScreen
import com.example.sabacontinuidad.ui.reporte.SeleccionEquipoScreen
import com.example.sabacontinuidad.viewmodel.AuthViewModel
import com.example.sabacontinuidad.viewmodel.IncidenteViewModel

object Rutas {
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val AYUDA = "ayuda"

    const val EQUIPO = "equipo"
    const val PROBLEMA = "problema"
    const val REVISION = "revision"
    const val ESCANER = "escaner"

    const val DETALLE_INCIDENTE = "detalle_incidente"
    const val SOLICITAR_SOPORTE = "solicitar_soporte"
    const val REGISTRAR_GESTION = "registrar_gestion"

    const val PERFIL = "perfil"
    const val APARIENCIA = "apariencia"
}

@Composable
fun AppNavigation(
    modo: ModoTema,
    alElegirModo: (ModoTema) -> Unit,
    authVm: AuthViewModel = viewModel(),
    incidenteVm: IncidenteViewModel = viewModel()
) {
    val autenticacion by authVm.estado.collectAsState()
    val formulario by incidenteVm.form.collectAsState()
    val usuario = autenticacion.usuario

    // Las pantallas abiertas reciben la selección actual al cambiar el tema.
    val modoActual by rememberUpdatedState(modo)

    LaunchedEffect(usuario) {
        incidenteVm.limpiar()
    }

    autenticacion.mensaje?.let { mensaje ->
        AlertDialog(
            onDismissRequest = { authVm.cerrarMensaje() },
            title = { Text("Acceso a Saba") },
            text = { Text(mensaje) },
            confirmButton = {
                TextButton(onClick = { authVm.cerrarMensaje() }) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Cada sesión tiene su propio historial de navegación.
    key(usuario) {
        val nav = rememberNavController()
        var mostrarManuales by rememberSaveable { mutableStateOf(false) }

        fun abrir(ruta: String) {
            nav.navigate(ruta) {
                launchSingleTop = true
            }
        }

        fun abrirPestana(ruta: String) {
            nav.navigate(ruta) {
                popUpTo(Rutas.INICIO) { inclusive = false }
                launchSingleTop = true
            }
        }

        val volver: () -> Unit = { nav.popBackStack() }

        val navegarBarra: (Int) -> Unit = { indice ->
            when (indice) {
                0 -> abrirPestana(Rutas.INICIO)
                1 -> abrirPestana(Rutas.DETALLE_INCIDENTE)
                2 -> mostrarManuales = true
                3 -> abrirPestana(Rutas.PERFIL)
            }
        }

        if (mostrarManuales) {
            AlertDialog(
                onDismissRequest = { mostrarManuales = false },
                title = { Text("Manuales") },
                text = { Text("Los manuales del parking aún están pendientes de incorporar.") },
                confirmButton = {
                    TextButton(onClick = { mostrarManuales = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }

        NavHost(
            navController = nav,
            startDestination = if (usuario == null) Rutas.LOGIN else Rutas.INICIO,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it / 4 }) + fadeIn()
            },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it / 4 }) + fadeOut()
            }
        ) {
            // Disponible antes y después de iniciar sesión.
            composable(Rutas.AYUDA) {
                AyudaAccesoScreen(alVolver = volver)
            }

            if (usuario == null) {
                composable(Rutas.LOGIN) {
                    LoginScreen(
                        cargando = autenticacion.cargando,
                        alPresionarMicrosoft = { authVm.iniciarSesion() },
                        alPresionarAyuda = { abrir(Rutas.AYUDA) }
                    )
                }
            } else {
                // Inicio
                composable(Rutas.INICIO) {
                    InicioScreen(
                        usuario = usuario,
                        incidentes = emptyList(),
                        errorCarga = null,
                        aviso = null,
                        alCerrarAviso = {},
                        alReportar = { abrir(Rutas.EQUIPO) },
                        alCerrarSesion = { authVm.cerrarSesion() },
                        alEscanear = { abrir(Rutas.ESCANER) },
                        alVerIncidentes = { abrirPestana(Rutas.DETALLE_INCIDENTE) },
                        alAbrirConfiguraciones = { abrirPestana(Rutas.PERFIL) }
                    )
                }

                // S23 · Perfil
                composable(Rutas.PERFIL) {
                    PerfilScreen(
                        usuario = usuario,
                        modo = modoActual,
                        alAbrirApariencia = { abrir(Rutas.APARIENCIA) },
                        alAbrirAyuda = { abrir(Rutas.AYUDA) },
                        alCerrarSesion = { authVm.cerrarSesion() },
                        barraInferior = {
                            IncBottomBar(selected = 3, onSelect = navegarBarra)
                        }
                    )
                }

                // S22 · Apariencia
                composable(Rutas.APARIENCIA) {
                    AparienciaScreen(
                        usuario = usuario,
                        modo = modoActual,
                        alElegirModo = alElegirModo,
                        alVolver = volver,
                        barraInferior = {
                            IncBottomBar(selected = 3, onSelect = navegarBarra)
                        }
                    )
                }

                // S04 · Selección de equipo
                composable(Rutas.EQUIPO) {
                    SeleccionEquipoScreen(
                        codigoActual = formulario.equipo,
                        alElegirEquipo = { codigo -> incidenteVm.onEquipo(codigo) },
                        alContinuar = { abrir(Rutas.PROBLEMA) },
                        alVolver = volver
                    )
                }

                // S05 · Descripción del problema
                composable(Rutas.PROBLEMA) {
                    ProblemaScreen(
                        vm = incidenteVm,
                        alCambiarEquipo = volver,
                        alContinuar = { abrir(Rutas.REVISION) },
                        alVolver = volver
                    )
                }

                // S06 · Revisión del reporte
                composable(Rutas.REVISION) {
                    RevisionReporteScreen(
                        vm = incidenteVm,
                        alVolver = volver,
                        alEditar = volver,
                        alEnviado = { abrirPestana(Rutas.INICIO) }
                    )
                }

                // S07 · Detalle del incidente
                composable(Rutas.DETALLE_INCIDENTE) {
                    IncidentDetailScreen(
                        onBack = volver,
                        onRequestSupport = { abrir(Rutas.SOLICITAR_SOPORTE) },
                        onRegisterAction = { abrir(Rutas.REGISTRAR_GESTION) },
                        onScanQr = { abrir(Rutas.ESCANER) },
                        onNavSelect = navegarBarra
                    )
                }

                // S08 · Solicitar soporte
                composable(Rutas.SOLICITAR_SOPORTE) {
                    SupportRequestScreen(
                        onBack = volver,
                        onOpenIncident = volver,
                        onRegisterManagement = { abrir(Rutas.REGISTRAR_GESTION) }
                    )
                }

                // S09 · Registrar gestión
                composable(Rutas.REGISTRAR_GESTION) {
                    RegisterManagementScreen(
                        onBack = volver
                    )
                }

                composable(Rutas.ESCANER) {
                    EscanerQrScreen(
                        alLeerCodigo = { codigo ->
                            incidenteVm.onEquipo(codigo)
                            volver()
                        },
                        alVolver = volver
                    )
                }
            }
        }
    }
}