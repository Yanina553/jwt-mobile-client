package com.example.jwtclient

import retrofit2.Call
import retrofit2.http.*

data class AuthRequest(val username: String, val password: String)
data class AuthResponse(val token: String)
data class MessageResponse(val message: String)

interface ApiService {
    @POST("/register")
    fun register(@Body request: AuthRequest): Call<MessageResponse>

    @POST("/login")
    fun login(@Body request: AuthRequest): Call<AuthResponse>

    @GET("/protected")
    fun getProtectedData(@Header("Authorization") token: String): Call<MessageResponse>
}
