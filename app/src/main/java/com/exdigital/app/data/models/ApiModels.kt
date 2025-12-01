package com.exdigital.app.data.models

import com.google.gson.annotations.SerializedName

// Modelo para recibir órdenes desde Supabase
data class OrderResponse(
    val id: Long,

    @SerializedName("usuario_id")
    val usuarioId: String,

    @SerializedName("total")
    val total: Double,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("created_at")
    val createdAt: String, // ISO timestamp

    @SerializedName("items")
    val items: String? // JSON serializado de items
)

// Modelo para crear una orden
data class CreateOrderRequest(
    @SerializedName("usuario_id")
    val usuarioId: String,

    @SerializedName("total")
    val total: Double,

    @SerializedName("estado")
    val estado: String = "PAGADO",

    @SerializedName("items")
    val items: String // JSON serializado de items
)

// Modelo para crear un producto
data class CreateProductRequest(
    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("precio")
    val precio: Double,

    @SerializedName("stock")
    val stock: Int,

    @SerializedName("imagenUrl")
    val imagenUrl: String?
)

