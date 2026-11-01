package com.example.sabacontinuidad.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sabacontinuidad.data.Incidente

@Entity(tableName = "incidentes")
data class IncidenteEntity(
    @PrimaryKey val id: String,
    val equipo: String,
    val area: String,
    val problema: String,
    val descripcion: String,
    val fecha: String,
    val pendiente: Boolean = false,
    val codigoEquipo: String = ""
)

fun IncidenteEntity.aModelo() = Incidente(
    id = id,
    equipo = equipo,
    area = area,
    problema = problema,
    descripcion = descripcion,
    fecha = fecha,
    pendiente = pendiente
)

fun Incidente.aEntidad() = IncidenteEntity(
    id = id,
    equipo = equipo,
    area = area,
    problema = problema,
    descripcion = descripcion,
    fecha = fecha,
    pendiente = pendiente
)