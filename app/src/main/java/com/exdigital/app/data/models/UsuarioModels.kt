package com.exdigital.app.data.models

import com.google.gson.annotations.SerializedName

// Modelo para recibir usuarios desde Supabase
data class UsuarioResponse(
    val id: Long,

    @SerializedName("email")
    val email: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("telefono")
    val telefono: String?,

    @SerializedName("direccion")
    val direccion: String?,

    @SerializedName("rol")
    val rol: String,

    @SerializedName("created_at")
    val createdAt: String?
)

// Modelo para registro de usuario
data class RegistroRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("telefono")
    val telefono: String?,

    @SerializedName("direccion")
    val direccion: String?
)

// Modelo para login
data class LoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)

// Modelo de respuesta de login
data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val usuario: UsuarioResponse?
)

