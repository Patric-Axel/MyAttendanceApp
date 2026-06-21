package com.proyecto.myattendanceapp.data.model

data class EntradaRequest(
    val idusuario: Int,
    val latitudentrada: Double?,
    val longitudentrada: Double?,
    val direntrada: String?,
    val fotoentrada: String?
)

