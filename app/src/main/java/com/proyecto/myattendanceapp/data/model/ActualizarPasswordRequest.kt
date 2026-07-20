package com.proyecto.myattendanceapp.data.model

data class ActualizarPasswordRequest(
    val passwordActual: String,
    val passwordNueva: String,
    val confirmarPassword: String
)