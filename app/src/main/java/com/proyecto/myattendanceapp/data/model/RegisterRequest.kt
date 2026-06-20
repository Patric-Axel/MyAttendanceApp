package com.proyecto.myattendanceapp.data.model

data class RegisterRequest(
    val nombres: String,
    val apellidos: String,
    val email: String,
    val celular: String,
    val password: String,
    val direccion: String
)
