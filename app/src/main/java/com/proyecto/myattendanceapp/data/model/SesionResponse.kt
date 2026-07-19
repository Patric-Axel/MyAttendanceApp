package com.proyecto.myattendanceapp.data.model


data class SesionResponse(
    val success: Boolean,
    val message: String,
    val userId: Int?,
    val nombres: String?,
    val apellidos: String?,
    val email: String?,
    val celular: String?,
    val direccion: String?,
    val fotoperfil: String?,
    val idrol: Int?,
    val idestado: Int?
)

