package com.proyecto.myattendanceapp.data.model

data class HistorialAsistenciaResponse(
    val idasistencia: Int,
    val fecha: String?,
    val horaentrada: String?,
    val horasalida: String?,
    val horastrabajadas: Double?,
    val estado: String?
)
