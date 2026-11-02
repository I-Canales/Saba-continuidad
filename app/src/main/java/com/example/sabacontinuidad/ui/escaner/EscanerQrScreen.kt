package com.example.sabacontinuidad.ui.escaner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EscanerQrScreen(
    alLeerCodigo: (String) -> Unit,
    alVolver: () -> Unit
) {
    val contexto = LocalContext.current
    val haptico = LocalHapticFeedback.current

    var tienePermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(contexto, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    var permisoRechazado by remember { mutableStateOf(false) }
    var codigoLeido by remember { mutableStateOf<String?>(null) }

    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        tienePermiso = concedido
        permisoRechazado = !concedido
    }

    LaunchedEffect(Unit) {
        if (!tienePermiso) pedirPermiso.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escanear QR") },
                navigationIcon = {
                    TextButton(onClick = alVolver) {
                        Text("‹ Volver", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Centra el código del equipo en el área de lectura.",
                style = MaterialTheme.typography.bodyMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (tienePermiso) {
                    VistaCamara(
                        alDetectar = { valor ->
                            codigoLeido = valor
                            haptico.performHapticFeedback(HapticFeedbackType.LongPress)
                            alLeerCodigo(valor)
                        }
                    )
                } else {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (permisoRechazado) {
                                "Sin permiso de cámara. Puedes darlo aquí o escribir el código a mano."
                            } else {
                                "Solicitando permiso de cámara…"
                            },
                            textAlign = TextAlign.Center
                        )
                        if (permisoRechazado) {
                            Button(onClick = { pedirPermiso.launch(Manifest.permission.CAMERA) }) {
                                Text("Dar permiso")
                            }
                        }
                    }
                }
            }

            Text(
                text = codigoLeido?.let { "Código leído · $it" }
                    ?: "Apunta la cámara al QR pegado en el equipo.",
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = alVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Escribir el código a mano")
            }
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
private fun VistaCamara(alDetectar: (String) -> Unit) {
    val contexto = LocalContext.current
    val ciclo = LocalActivity.current as? LifecycleOwner ?: return

    val vista = remember {
        PreviewView(contexto).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    val ejecutor = remember { Executors.newSingleThreadExecutor() }
    val lector = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )
    }
    val yaLeido = remember { AtomicBoolean(false) }

    DisposableEffect(ciclo) {
        var proveedor: ProcessCameraProvider? = null
        val futuro = ProcessCameraProvider.getInstance(contexto)

        futuro.addListener({
            val camara = futuro.get()
            proveedor = camara

            val vistaPrevia = Preview.Builder().build().also {
                it.surfaceProvider = vista.surfaceProvider
            }

            val analisis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analisis.setAnalyzer(ejecutor) { imagen ->
                val media = imagen.image
                if (media == null || yaLeido.get()) {
                    imagen.close()
                    return@setAnalyzer
                }

                val entrada = InputImage.fromMediaImage(media, imagen.imageInfo.rotationDegrees)
                lector.process(entrada)
                    .addOnSuccessListener { codigos ->
                        val valor = codigos.firstOrNull()?.rawValue?.trim()
                        if (!valor.isNullOrBlank() && yaLeido.compareAndSet(false, true)) {
                            alDetectar(valor)
                        }
                    }
                    .addOnCompleteListener { imagen.close() }
            }

            camara.unbindAll()
            camara.bindToLifecycle(
                ciclo,
                CameraSelector.DEFAULT_BACK_CAMERA,
                vistaPrevia,
                analisis
            )
        }, ContextCompat.getMainExecutor(contexto))

        onDispose {
            proveedor?.unbindAll()
            ejecutor.shutdown()
            lector.close()
        }
    }

    AndroidView(factory = { vista }, modifier = Modifier.fillMaxSize())
}