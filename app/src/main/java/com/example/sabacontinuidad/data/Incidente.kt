package com.example.sabacontinuidad.data

data class Incidente(
    val id: String,
    val equipo: String,
    val area: String,
    val problema: String,
    val descripcion: String,
    val fecha: String,
    val pendiente: Boolean = false
)