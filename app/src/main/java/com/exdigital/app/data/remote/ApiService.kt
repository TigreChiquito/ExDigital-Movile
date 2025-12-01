package com.exdigital.app.data.remote

import com.exdigital.app.data.models.ProductResponse
import com.exdigital.app.data.models.OrderResponse
import com.exdigital.app.data.models.CreateProductRequest
import com.exdigital.app.data.models.CreateOrderRequest
import com.exdigital.app.data.models.RegistroRequest
import com.exdigital.app.data.models.LoginRequest
import com.exdigital.app.data.models.LoginResponse
import com.exdigital.app.data.models.UsuarioResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    // Productos
    @GET("/api/productos")
    fun obtenerProductos(): Call<List<ProductResponse>>

    @POST("/api/productos")
    fun crearProducto(@Body producto: CreateProductRequest): Call<ProductResponse>

    // Órdenes
    @GET("/api/ordenes")
    fun obtenerOrdenes(): Call<List<OrderResponse>>

    @POST("/api/ordenes")
    fun crearOrden(@Body orden: CreateOrderRequest): Call<OrderResponse>

    // Usuarios
    @POST("/api/usuarios/registro")
    fun registrarUsuario(@Body registro: RegistroRequest): Call<UsuarioResponse>

    @POST("/api/usuarios/login")
    fun loginUsuario(@Body login: LoginRequest): Call<LoginResponse>

    @GET("/api/usuarios")
    fun obtenerUsuarios(): Call<List<UsuarioResponse>>

    @GET("/api/usuarios/email/{email}")
    fun obtenerUsuarioPorEmail(@Path("email") email: String): Call<UsuarioResponse>
}