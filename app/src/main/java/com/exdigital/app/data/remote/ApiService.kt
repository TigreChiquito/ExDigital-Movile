package com.exdigital.app.data.remote

import com.exdigital.app.data.models.ProductResponse
import com.exdigital.app.data.models.OrderResponse
import com.exdigital.app.data.models.CreateProductRequest
import com.exdigital.app.data.models.CreateOrderRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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
}