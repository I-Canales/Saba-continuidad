package com.example.sabacontinuidad.ui.reporte

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sabacontinuidad.viewmodel.IncidenteViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


private val VerdeS06 = Color(0xFF0E5A47)
private val TextoS06 = Color(0xFF14231E)
private val GrisS06 = Color(0xFF5E6B65)
private val BordeS06 = Color(0xFFD9E1DD)
private val RojoS06 = Color(0xFFB32323)


@Composable
fun RevisionReporteScreen(
    vm: IncidenteViewModel,
    alVolver: () -> Unit,
    alEditar: () -> Unit,
    alEnviado: () -> Unit,
    alLlamarSoporte: () -> Unit = {},
    parking: String = "Demo Centro",
    nombreReportante: String = "Modo demostración",
    fotoAdjunta: Boolean = false
) {
    val formulario by vm.form.collectAsState()
    val cargando by vm.cargando.collectAsState()
    val fecha = remember {
        LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm", Locale("es", "CL"))
        )
    }
    val equipo = when (formulario.equipo.trim().uppercase()) {
        "CAJ-04" -> "Cajero 4" to "CAJ-04 · Nivel -1"
        "SAL-02" -> "Barrera de salida 2" to "SAL-02 · Salida"
        else -> (formulario.equipo.ifBlank { "Sin seleccionar" }) to formulario.equipo
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).clickable(onClick = alVolver),
                contentAlignment = Alignment.Center
            ) {
                Text("‹", fontSize = 29.sp, color = TextoS06)
            }
            Text(
                "Revisar reporte",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextoS06
            )
            Spacer(Modifier.size(36.dp))
        }

        Spacer(Modifier.height(8.dp))
        PasosRevision()
        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .border(1.dp, BordeS06, RoundedCornerShape(12.dp))
                    .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp)
            ) {
                Text("Resumen del reporte", fontSize = 19.sp, color = TextoS06, fontWeight = FontWeight.Bold)
                FilaRevision("⌖", "Parking", parking)
                FilaRevision("▣", "Equipo", equipo.first, detalle = equipo.second)
                FilaRevision("⚠", "Problema", formulario.problema.ifBlank { "Sin seleccionar" })
                FilaRevision("▧", "Fotografía", if (fotoAdjunta) "Fotografía adjunta" else "Sin fotografía adjunta")
                FilaRevision("☷", "Descripción", formulario.descripcion.ifBlank { "Sin descripción" }, negrita = false)
                FilaRevision("♙", "Reporta", nombreReportante)
                FilaRevision("▦", "Fecha y hora", fecha, negrita = false)
                FilaRevision("☷", "Origen", "reporte manual", negrita = false, divider = false)

                Row(
                    modifier = Modifier.clickable(onClick = alEditar).padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✎", color = VerdeS06, fontSize = 17.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("Editar información", color = VerdeS06, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Text(
            "ⓘ  Recibirás un número de caso al enviarlo.",
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            color = GrisS06, textAlign = TextAlign.Center, fontSize = 12.sp
        )
        Text(
            "♧  ¿Es urgente? Llamar a soporte",
            modifier = Modifier.fillMaxWidth().clickable(onClick = alLlamarSoporte)
                .padding(top = 6.dp, bottom = 8.dp),
            color = RojoS06, textAlign = TextAlign.Center, fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        formulario.errorServidor?.let { error ->
            Text(
                error,
                color = RojoS06,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
            )
        }
        if (formulario.intentoEnviar && !formulario.valido) {
            Text(
                "Revisa los campos requeridos en el paso anterior antes de enviar.",
                color = RojoS06,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
            )
        }

        Button(
            onClick = { vm.enviar(alEnviado) },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeS06)
        ) {
            Text(if (cargando) "Enviando…" else "➤  Enviar reporte", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(14.dp))
    }
}

@Composable
private fun PasosRevision() {
    Column(modifier = Modifier.fillMaxWidth().height(76.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CirculoPaso(completo = true, numero = 1)
            Box(Modifier.weight(1f).height(2.dp).background(VerdeS06))
            CirculoPaso(completo = true, numero = 2)
            Box(Modifier.weight(1f).height(2.dp).background(VerdeS06))
            CirculoPaso(completo = false, numero = 3)
        }
        Spacer(Modifier.height(5.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("Equipo", "Problema", "Revisión").forEachIndexed { i, texto ->
                Text(
                    texto,
                    modifier = Modifier.width(90.dp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = if (i == 2) FontWeight.Bold else FontWeight.Medium,
                    color = VerdeS06
                )
            }
        }
    }
}

@Composable
private fun CirculoPaso(completo: Boolean, numero: Int) {
    Box(
        modifier = Modifier.size(40.dp)
            .border(if (completo) 2.dp else 3.dp, VerdeS06, CircleShape)
            .background(if (completo) VerdeS06 else Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            if (completo) "✓" else numero.toString(),
            color = if (completo) Color.White else VerdeS06,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FilaRevision(
    icono: String,
    etiqueta: String,
    valor: String,
    detalle: String? = null,
    negrita: Boolean = true,
    divider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 39.dp).padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icono, modifier = Modifier.width(22.dp), fontSize = 18.sp, color = TextoS06, textAlign = TextAlign.Center)
            Spacer(Modifier.width(10.dp))
            Text(etiqueta, modifier = Modifier.width(78.dp), color = GrisS06, fontSize = 13.sp)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    valor,
                    color = TextoS06,
                    fontSize = 14.sp,
                    fontWeight = if (negrita) FontWeight.SemiBold else FontWeight.Normal,
                    lineHeight = 19.sp
                )
                if (!detalle.isNullOrBlank()) Text(detalle, color = GrisS06, fontSize = 12.sp)
            }
        }
        if (divider) HorizontalDivider(thickness = 1.dp, color = BordeS06)
    }
}
