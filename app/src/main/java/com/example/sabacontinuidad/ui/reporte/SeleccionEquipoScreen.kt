package com.example.sabacontinuidad.ui.reporte

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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val Verde = Color(0xFF0D5B47)
private val VerdeSuave = Color(0xFFE1F1EB)
private val Texto = Color(0xFF14231E)
private val Secundario = Color(0xFF7D8884)
private val Borde = Color(0xFFDCE4DF)
private val Fondo = Color.White

private data class EquipoOpcion(val nombre: String, val codigo: String, val detalle: String, val barrera: Boolean)

private val equiposDemo = listOf(
    EquipoOpcion("Cajero 4", "CAJ-04", "Nivel -1", barrera = false),
    EquipoOpcion("Barrera de salida 2", "SAL-02", "Salida", barrera = true)
)


@Composable
fun SeleccionEquipoScreen(
    codigoActual: String,
    alElegirEquipo: (String) -> Unit,
    alContinuar: () -> Unit,
    alVolver: () -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    var menuParking by remember { mutableStateOf(false) }
    var parking by remember { mutableStateOf("Demo Centro") }

    LaunchedEffect(codigoActual) {
        if (codigoActual.isBlank()) alElegirEquipo("CAJ-04")
    }

    val visibles = remember(busqueda) {
        equiposDemo.filter {
            it.nombre.contains(busqueda, ignoreCase = true) ||
                    it.codigo.contains(busqueda, ignoreCase = true) ||
                    it.detalle.contains(busqueda, ignoreCase = true)
        }
    }
    val codigoExterno = codigoActual.isNotBlank() && equiposDemo.none { it.codigo == codigoActual }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Row(
            Modifier.fillMaxWidth().height(54.dp).padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).clickable(onClick = alVolver),
                contentAlignment = Alignment.Center
            ) {
                Text("‹", color = Texto, fontSize = 29.sp, lineHeight = 31.sp)
            }
            Text(
                "Reportar problema",
                modifier = Modifier.weight(1f).padding(end = 36.dp),
                color = Texto, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 52.dp, end = 52.dp, top = 5.dp, bottom = 15.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ) {
            Paso(1, "Equipo", activo = true, modifier = Modifier.width(55.dp))
            LineaPaso(Modifier.weight(1f))
            Paso(2, "Problema", activo = false, modifier = Modifier.width(69.dp))
            LineaPaso(Modifier.weight(1f))
            Paso(3, "Revisión", activo = false, modifier = Modifier.width(63.dp))
        }

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(horizontal = 46.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Text("Selecciona el parking", color = Texto, fontSize = 14.sp, fontWeight = FontWeight.Bold)

            Box {
                Row(
                    modifier = Modifier.fillMaxWidth().height(43.dp)
                        .border(1.dp, Borde, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { menuParking = true }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Simbolo(TipoSimbolo.UBICACION, Modifier.size(19.dp), Verde)
                    Spacer(Modifier.width(10.dp))
                    Text(parking, color = Texto, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Simbolo(TipoSimbolo.ABAJO, Modifier.size(16.dp), Texto)
                }
                DropdownMenu(expanded = menuParking, onDismissRequest = { menuParking = false }) {
                    DropdownMenuItem(text = { Text("Demo Centro") }, onClick = {
                        parking = "Demo Centro"
                        menuParking = false
                    })
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.weight(1f).height(1.dp).background(Borde))
                Text("o busca un equipo", modifier = Modifier.padding(horizontal = 9.dp),
                    fontSize = 10.sp, color = Secundario)
                Box(Modifier.weight(1f).height(1.dp).background(Borde))
            }

            Row(
                modifier = Modifier.fillMaxWidth().height(43.dp)
                    .border(1.dp, Borde, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Simbolo(TipoSimbolo.BUSCAR, Modifier.size(18.dp), Texto)
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = busqueda, onValueChange = { busqueda = it }, singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = Texto, fontSize = 13.sp),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (busqueda.isEmpty()) Text("Buscar por nombre o código...", color = Secundario, fontSize = 12.sp)
                            inner()
                        }
                    }
                )
                if (busqueda.isNotEmpty()) {
                    Text("×", modifier = Modifier.clickable { busqueda = "" }.padding(3.dp),
                        color = Secundario, fontSize = 22.sp)
                }
            }

            visibles.forEach { equipo ->
                val marcado = codigoActual.equals(equipo.codigo, ignoreCase = true)
                FilaEquipo(
                    nombre = equipo.nombre, codigo = equipo.codigo,
                    detalle = equipo.detalle, barrera = equipo.barrera,
                    seleccionado = marcado,
                    onClick = { alElegirEquipo(equipo.codigo) }
                )
            }
            if (visibles.isEmpty()) {
                Text("No se encontraron equipos con esa búsqueda.", fontSize = 12.sp,
                    color = Secundario, modifier = Modifier.padding(vertical = 12.dp))
            }
            if (codigoExterno) {
                FilaEquipo(nombre = "Equipo seleccionado", codigo = codigoActual,
                    detalle = "Código ingresado previamente", barrera = false,
                    seleccionado = true, onClick = { alElegirEquipo(codigoActual) })
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 46.dp)
                .padding(top = 10.dp, bottom = 34.dp)
                .height(45.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(if (codigoActual.isBlank()) Verde.copy(alpha = .55f) else Verde)
                .clickable(enabled = codigoActual.isNotBlank(), onClick = alContinuar),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Continuar", color = Color.White, fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(10.dp))
                Text("›", color = Color.White, fontSize = 22.sp, lineHeight = 20.sp)
            }
        }
    }
}

@Composable
private fun Paso(numero: Int, etiqueta: String, activo: Boolean, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(31.dp)
                .border(if (activo) 2.dp else 1.dp, if (activo) Verde else Borde, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(numero.toString(), fontSize = 14.sp,
                fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal,
                color = if (activo) Verde else Secundario)
        }
        Spacer(Modifier.height(5.dp))
        Text(etiqueta, color = if (activo) Verde else Secundario, fontSize = 11.sp,
            fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
private fun LineaPaso(modifier: Modifier) {
    Box(modifier.padding(top = 15.dp).height(1.dp).background(Borde))
}

@Composable
private fun FilaEquipo(
    nombre: String,
    codigo: String,
    detalle: String,
    barrera: Boolean,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(60.dp)
            .border(if (seleccionado) 1.4.dp else 1.dp, if (seleccionado) Verde else Borde,
                RoundedCornerShape(9.dp))
            .clip(RoundedCornerShape(9.dp))
            .background(if (seleccionado) VerdeSuave else Fondo)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Simbolo(if (barrera) TipoSimbolo.BARRERA else TipoSimbolo.CAJERO,
            Modifier.size(25.dp), Texto)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Text(nombre, color = Texto, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                lineHeight = 17.sp)
            Text("$codigo · $detalle", color = Secundario, fontSize = 11.sp,
                lineHeight = 14.sp)
        }
        Box(Modifier.size(20.dp)
            .border(if (seleccionado) 1.7.dp else 1.3.dp,
                if (seleccionado) Verde else Secundario, CircleShape),
            contentAlignment = Alignment.Center) {
            if (seleccionado) Box(Modifier.size(10.dp).background(Verde, CircleShape))
        }
    }
}

private enum class TipoSimbolo { UBICACION, ABAJO, BUSCAR, CAJERO, BARRERA }

@Composable
private fun Simbolo(tipo: TipoSimbolo, modifier: Modifier, color: Color) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val ancho = 1.8.dp.toPx()
        when (tipo) {
            TipoSimbolo.ABAJO -> {
                drawLine(color, Offset(w * .23f, h * .40f), Offset(w * .5f, h * .64f), ancho, StrokeCap.Round)
                drawLine(color, Offset(w * .5f, h * .64f), Offset(w * .77f, h * .40f), ancho, StrokeCap.Round)
            }
            TipoSimbolo.BUSCAR -> {
                drawCircle(color, radius = w * .30f, center = Offset(w * .42f, h * .42f), style = Stroke(ancho))
                drawLine(color, Offset(w * .64f, h * .66f), Offset(w * .9f, h * .92f), ancho, StrokeCap.Round)
            }
            TipoSimbolo.UBICACION -> {
                val p = Path().apply {
                    moveTo(w * .5f, h * .95f)
                    cubicTo(w * .12f, h * .58f, w * .19f, h * .1f, w * .5f, h * .1f)
                    cubicTo(w * .81f, h * .1f, w * .88f, h * .58f, w * .5f, h * .95f)
                    close()
                }
                drawPath(p, color, style = Stroke(ancho))
                drawCircle(color, radius = w * .115f, center = Offset(w * .5f, h * .43f), style = Stroke(ancho))
            }
            TipoSimbolo.CAJERO -> {
                drawRoundRect(color, topLeft = Offset(w * .27f, h * .07f),
                    size = androidx.compose.ui.geometry.Size(w * .49f, h * .85f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
                    style = Stroke(ancho))
                drawLine(color, Offset(w * .36f, h * .32f), Offset(w * .66f, h * .32f), ancho)
                drawLine(color, Offset(w * .36f, h * .47f), Offset(w * .66f, h * .47f), ancho)
                drawLine(color, Offset(w * .4f, h * .73f), Offset(w * .62f, h * .73f), ancho)
            }
            TipoSimbolo.BARRERA -> {
                drawRect(color, topLeft = Offset(w * .1f, h * .36f),
                    size = androidx.compose.ui.geometry.Size(w * .22f, h * .36f), style = Stroke(ancho))
                drawLine(color, Offset(w * .32f, h * .42f), Offset(w * .95f, h * .42f), ancho)
                drawLine(color, Offset(w * .32f, h * .52f), Offset(w * .95f, h * .52f), ancho)
                drawLine(color, Offset(w * .48f, h * .42f), Offset(w * .48f, h * .52f), ancho)
                drawLine(color, Offset(w * .75f, h * .42f), Offset(w * .75f, h * .52f), ancho)
            }
        }
    }
}
