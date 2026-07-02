package com.proyecto.myattendanceapp.data.model

data class DetalleAsistenciaResponse(

    val idasistencia: Int,
    val fecha: String?,
    val fecharegistro: String?,
    val horaentrada: String?,
    val horasalida: String?,
    val horastrabajadas: Double?,
    val latitudentrada: Double?,
    val longitudentrada: Double?,
    val latitudsalida: Double?,
    val longitudsalida: Double?,
    val direntrada: String?,
    val dirsalida: String?,
    val fotoentrada: String?,
    val fotosalida: String?,
    val estado: String?,
    val observacion: String?
)
