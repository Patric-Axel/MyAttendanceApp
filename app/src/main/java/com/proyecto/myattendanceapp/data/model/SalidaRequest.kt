package com.proyecto.myattendanceapp.data.model

data class SalidaRequest(
    val idusuario: Int,
    val latitudsalida: Double?,
    val longitudsalida: Double?,
    val dirsalida: String?,
    val fotosalida: String?
)
