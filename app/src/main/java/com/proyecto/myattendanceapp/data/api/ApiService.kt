package com.proyecto.myattendanceapp.data.api

import com.proyecto.myattendanceapp.data.model.LoginRequest
import com.proyecto.myattendanceapp.data.model.RegisterRequest
import com.proyecto.myattendanceapp.data.model.SesionResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<SesionResponse>

    @POST("api/auth/register")
    fun register(@Body request: RegisterRequest): Call<SesionResponse>
}