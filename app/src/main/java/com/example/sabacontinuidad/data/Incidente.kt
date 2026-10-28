package com.example.sabacontinuidad.data

data class Incidente(
    val id: Long,
    val equipo: String,
    val area: String,
    val problema: String,
    val descripcion: String,
    val fecha: Long
)