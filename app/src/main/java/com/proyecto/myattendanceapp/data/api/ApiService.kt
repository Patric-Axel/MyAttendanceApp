package com.proyecto.myattendanceapp.data.api

import com.proyecto.myattendanceapp.data.model.ActualizarPasswordRequest
import com.proyecto.myattendanceapp.data.model.ActualizarPerfilRequest
import com.proyecto.myattendanceapp.data.model.AsistenciaHoyResponse
import com.proyecto.myattendanceapp.data.model.AsistenciaResponse
import com.proyecto.myattendanceapp.data.model.DetalleAsistenciaResponse
import com.proyecto.myattendanceapp.data.model.EntradaRequest
import com.proyecto.myattendanceapp.data.model.HistorialAsistenciaResponse
import com.proyecto.myattendanceapp.data.model.LoginRequest
import com.proyecto.myattendanceapp.data.model.PerfilResponse
import com.proyecto.myattendanceapp.data.model.RegisterRequest
import com.proyecto.myattendanceapp.data.model.SalidaRequest
import com.proyecto.myattendanceapp.data.model.SesionResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<SesionResponse>

    @POST("api/auth/register")
    fun register(@Body request: RegisterRequest): Call<SesionResponse>

    @GET("api/asistencia/hoy/{idusuario}")
    fun obtenerAsistenciaHoy(@Path("idusuario") idusuario: Int): Call<AsistenciaHoyResponse>

    @POST("api/asistencia/entrada")
    fun registrarEntrada(@Body request: EntradaRequest): Call<AsistenciaResponse>

    @POST("api/asistencia/salida")
    fun registrarSalida(@Body request: SalidaRequest): Call<AsistenciaResponse>

    //HISTORIAL
    @GET("api/asistencia/usuario/{idusuario}/historial")
    fun listarHistorial(
        @Path("idusuario") idusuario: Int
    ): Call<List<HistorialAsistenciaResponse>>

    //DETALLE HISTORIAL
    @GET("api/asistencia/{idasistencia}/detalle")
    fun obtenerDetalleAsistencia(
        @Path("idasistencia") idasistencia: Int
    ): Call<DetalleAsistenciaResponse>

    //ACTUALIZAR PERFIL NOMBRE Y DIRECCION
    @PUT("api/actualizar/{idusuario}")
    fun actualizarPerfil(
        @Path("idusuario") idusuario: Int,
        @Body request: ActualizarPerfilRequest
    ): Call<PerfilResponse>

    //actualizar contraseña
    @PATCH("api/actualizar/{idusuario}/password")
    fun actualizarPassword(
        @Path("idusuario") idusuario: Int,
        @Body request: ActualizarPasswordRequest
    ): Call<PerfilResponse>
}