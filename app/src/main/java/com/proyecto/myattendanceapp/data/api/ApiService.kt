package com.proyecto.myattendanceapp.data.api

import com.proyecto.myattendanceapp.data.model.AsistenciaHoyResponse
import com.proyecto.myattendanceapp.data.model.AsistenciaResponse
import com.proyecto.myattendanceapp.data.model.EntradaRequest
import com.proyecto.myattendanceapp.data.model.LoginRequest
import com.proyecto.myattendanceapp.data.model.RegisterRequest
import com.proyecto.myattendanceapp.data.model.SalidaRequest
import com.proyecto.myattendanceapp.data.model.SesionResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
}