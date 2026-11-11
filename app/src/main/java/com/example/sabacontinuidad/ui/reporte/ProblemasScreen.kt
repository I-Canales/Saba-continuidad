package com.example.sabacontinuidad.ui.reporte

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.sabacontinuidad.viewmodel.IncidenteViewModel


private val S05Verde = Color(0xFF0E5A47)
private val S05VerdeSuave = Color(0xFFE2F1EA)
private val S05Texto = Color(0xFF14231E)
private val S05Gris = Color(0xFF5E6B65)
private val S05Borde = Color(0xFFD9E1DD)
private val S05Superficie = Color(0xFFF3F5F4)
private val S05Rojo = Color(0xFFB62222)

private val opcionesProblemaS05 = listOf("No imprime comprobante", "No acepta tarjeta", "Otro")

@Composable
fun ProblemaScreen(
    vm: IncidenteViewModel,
    alCambiarEquipo: () -> Unit,
    alContinuar: () -> Unit,
    alVolver: () -> Unit
) {
    val form by vm.form.collectAsState()
    val contexto = LocalContext.current
    var fotoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var mostrarFotoEjemplo by rememberSaveable { mutableStateOf(true) }
    var dialogo by remember { mutableStateOf<String?>(null) }
    var departamento by rememberSaveable { mutableStateOf<String?>(null) }
    var mostrarDepartamentos by remember { mutableStateOf(false) }

    val selectorFoto = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            fotoUri = uri
            mostrarFotoEjemplo = false
        }
    }
    val dictado = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        if (resultado.resultCode == Activity.RESULT_OK) {
            resultado.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()?.let(vm::agregarDictado)
        }
    }
    val codigo = form.equipo.ifBlank { "Sin seleccionar" }
    val barrera = codigo.startsWith("SAL-", ignoreCase = true)
    val nombreEquipo = when (codigo.uppercase()) {
        "CAJ-04" -> "Cajero 4"
        "SAL-02" -> "Barrera de salida 2"
        else -> "Equipo seleccionado"
    }
    val detalleEquipo = when (codigo.uppercase()) {
        "CAJ-04" -> "CAJ-04 · Nivel -1"
        "SAL-02" -> "SAL-02 · Salida"
        else -> codigo
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Row(Modifier.fillMaxWidth().height(48.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(36.dp).clip(CircleShape).clickable(onClick = alVolver),
                    contentAlignment = Alignment.Center) {
                    Text("‹", fontSize = 30.sp, color = S05Texto)
                }
                Text("Reportar problema", modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center, fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = S05Texto)
                Spacer(Modifier.width(36.dp))
            }
            Spacer(Modifier.height(7.dp))
            S05Stepper()
            Spacer(Modifier.height(7.dp))

            Column(
                Modifier.fillMaxWidth().border(1.dp, S05Borde, RoundedCornerShape(10.dp))
                    .background(S05Superficie, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text("Equipo seleccionado", color = S05Texto, fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (barrera) "⚑" else "▣", fontSize = 27.sp, color = S05Texto,
                        modifier = Modifier.width(34.dp), textAlign = TextAlign.Center)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(nombreEquipo, color = S05Texto, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(detalleEquipo, color = S05Gris, fontSize = 13.sp)
                    }
                    Box(
                        Modifier.height(34.dp).border(1.dp, S05Borde, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp)).background(Color.White)
                            .clickable(onClick = alCambiarEquipo).padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("Cambiar", fontSize = 14.sp, color = S05Texto, fontWeight = FontWeight.SemiBold) }
                }
            }
            Spacer(Modifier.height(7.dp))

            Text("¿Cuál es el problema?", color = S05Texto, fontSize = 17.sp,
                fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(7.dp))
            opcionesProblemaS05.forEach { opcion ->
                val seleccionado = form.problema == opcion ||
                        (form.problema.isBlank() && opcion == "No imprime comprobante")
                Row(
                    Modifier.fillMaxWidth().height(42.dp)
                        .border(1.dp, if (seleccionado) S05Verde else S05Borde, RoundedCornerShape(8.dp))
                        .background(if (seleccionado) S05VerdeSuave else S05Superficie, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { vm.onProblema(opcion) }
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(24.dp).border(2.dp, if (seleccionado) S05Verde else S05Gris, CircleShape),
                        contentAlignment = Alignment.Center) {
                        if (seleccionado) Box(Modifier.size(12.dp).background(S05Verde, CircleShape))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(opcion, fontSize = 15.sp, color = S05Texto,
                        fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal)
                }
                Spacer(Modifier.height(7.dp))
            }
            Spacer(Modifier.height(1.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Fotografía", color = S05Texto, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(" · opcional", color = S05Gris, fontSize = 13.sp)
            }
            Spacer(Modifier.height(7.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (fotoUri != null || mostrarFotoEjemplo) {
                    Box(Modifier.weight(1.26f).height(96.dp).clip(RoundedCornerShape(8.dp))) {
                        if (fotoUri != null) {
                            AndroidView(
                                factory = { c -> ImageView(c).apply { scaleType = ImageView.ScaleType.CENTER_CROP } },
                                update = { it.setImageURI(fotoUri) },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            S05DibujoCajero(Modifier.fillMaxSize())
                        }
                        Box(
                            Modifier.align(Alignment.TopEnd).padding(7.dp).size(25.dp)
                                .background(Color.White, CircleShape).clickable {
                                    fotoUri = null
                                    mostrarFotoEjemplo = false
                                }, contentAlignment = Alignment.Center
                        ) { Text("×", color = S05Texto, fontSize = 20.sp) }
                    }
                }
                Column(
                    Modifier.weight(1f).height(96.dp)
                        .border(1.dp, S05Gris, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectorFoto.launch("image/*") },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("▣", color = S05Texto, fontSize = 25.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Agregar fotografía", color = S05Gris, fontSize = 12.sp,
                        textAlign = TextAlign.Center)
                }
            }
            Spacer(Modifier.height(7.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Descripción", color = S05Texto, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("  opcional", color = S05Gris, fontSize = 13.sp)
            }
            Spacer(Modifier.height(7.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier.weight(1f).heightIn(min = 56.dp)
                        .border(1.dp, S05Borde, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 9.dp)
                ) {
                    BasicTextField(
                        value = form.descripcion,
                        onValueChange = vm::onDescripcion,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 14.sp, color = S05Texto, lineHeight = 19.sp),
                        minLines = 2, maxLines = 4,
                        decorationBox = { inner ->
                            if (form.descripcion.isEmpty()) {
                                Text("El pago finaliza, pero no sale el comprobante.",
                                    fontSize = 13.sp, lineHeight = 19.sp, color = S05Gris)
                            }
                            inner()
                        }
                    )
                }
                Column(
                    Modifier.width(60.dp).height(56.dp)
                        .border(1.dp, S05Borde, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-CL")
                            }
                            try { dictado.launch(intent) }
                            catch (_: ActivityNotFoundException) {
                                Toast.makeText(contexto, "Dictado no disponible en este dispositivo", Toast.LENGTH_LONG).show()
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("♩", color = S05Texto, fontSize = 23.sp)
                    Text("Dictar", color = S05Texto, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(7.dp))
            Text("ⓘ  Revisa el texto antes de enviar.", color = S05Gris, fontSize = 12.sp)
            Spacer(Modifier.height(7.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("▤ Consultar procedimiento", color = S05Verde, fontSize = 12.sp,
                    modifier = Modifier.clickable { dialogo = "Los procedimientos aún no están vinculados." })
                Text("☎ ¿Es urgente? Llamar a soporte", color = S05Rojo, fontSize = 11.sp,
                    modifier = Modifier.clickable { dialogo = "Los contactos de soporte aún no están configurados." })
            }
            Spacer(Modifier.height(18.dp))
        }

        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Box(Modifier.fillMaxWidth().height(52.dp)
                .background(S05Verde, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    if (form.problema.isBlank()) vm.onProblema("No imprime comprobante")
                    alContinuar()
                }, contentAlignment = Alignment.Center
            ) {
                Text("Continuar   ›", fontSize = 16.sp, color = Color.White,
                    fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(7.dp))
            Row(
                Modifier.fillMaxWidth().height(44.dp).border(1.dp, S05Borde, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { mostrarDepartamentos = true }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(departamento?.let { "Departamento: $it" } ?: "Asignar departamento",
                    color = S05Texto, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Text("›", color = S05Verde, fontSize = 25.sp)
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    if (mostrarDepartamentos) {
        AlertDialog(
            onDismissRequest = { mostrarDepartamentos = false },
            title = { Text("Asignar departamento") },
            text = {
                Column {
                    listOf("Operaciones", "Mantenimiento", "Soporte técnico", "Seguridad").forEach { nombre ->
                        Text(nombre, Modifier.fillMaxWidth().clickable {
                            departamento = nombre
                            mostrarDepartamentos = false
                        }.padding(12.dp))
                    }
                    Text("Selección visual: todavía no se guarda en la base de datos.",
                        fontSize = 12.sp, color = S05Gris)
                }
            },
            confirmButton = { TextButton(onClick = { mostrarDepartamentos = false }) { Text("Cerrar") } }
        )
    }
    dialogo?.let { mensaje ->
        AlertDialog(
            onDismissRequest = { dialogo = null },
            title = { Text("Próxima integración") },
            text = { Text(mensaje) },
            confirmButton = { TextButton(onClick = { dialogo = null }) { Text("Entendido") } }
        )
    }
}

@Composable
private fun S05Stepper() {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 25.dp), verticalAlignment = Alignment.CenterVertically) {
            S05PasoCirculo("✓", completado = true)
            Box(Modifier.weight(1f).height(2.dp).background(S05Verde))
            S05PasoCirculo("2", activo = true)
            Box(Modifier.weight(1f).height(2.dp).background(S05Borde))
            S05PasoCirculo("3")
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("Equipo", "Problema", "Revisión").forEachIndexed { i, nombre ->
                Box(Modifier.width(90.dp), contentAlignment = Alignment.Center) {
                    Text(nombre, color = if (i < 2) S05Verde else S05Gris,
                        fontSize = 13.sp, fontWeight = if (i == 1) FontWeight.Bold else FontWeight.Medium)
                }
            }
        }
        Spacer(Modifier.height(7.dp))
    }
}

@Composable
private fun S05PasoCirculo(texto: String, activo: Boolean = false, completado: Boolean = false) {
    Box(Modifier.size(40.dp).border(if (activo) 3.dp else 2.dp,
        if (activo || completado) S05Verde else S05Borde, CircleShape)
        .background(if (completado) S05Verde else Color.White, CircleShape),
        contentAlignment = Alignment.Center) {
        Text(texto, color = if (completado) Color.White else if (activo) S05Verde else S05Gris,
            fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun S05DibujoCajero(modifier: Modifier = Modifier) {
    Canvas(modifier.background(Color(0xFF3B4346))) {
        val sx = size.width / 190f
        val sy = size.height / 96f
        fun rect(c: Color, x: Float, y: Float, w: Float, h: Float) {
            drawRect(c, topLeft = Offset(x * sx, y * sy), size = Size(w * sx, h * sy))
        }
        rect(Color(0xFF596266), 0f, 0f, 190f, 29.54f)
        rect(Color(0xFF2A3033), 0f, 72.37f, 190f, 23.63f)
        rect(Color(0xFFE0B400), 14.68f, 29.54f, 11.42f, 53.17f)
        rect(Color(0xFFE0B400), 163.09f, 29.54f, 11.42f, 53.17f)
        rect(Color(0xFF6C7B82), 63.61f, 11.82f, 63.61f, 75.32f)
        rect(Color(0xFF7DCDE0), 71.76f, 19.2f, 47.3f, 26.58f)
        rect(Color(0xFF3D474B), 78.28f, 53.17f, 34.25f, 5.91f)
        rect(Color(0xFF3D474B), 78.28f, 64.98f, 34.25f, 5.91f)
        rect(Color(0xFFE0B400), 32.62f, 33.97f, 8.15f, 44.31f)
    }
}
