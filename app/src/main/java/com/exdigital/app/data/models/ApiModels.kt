package com.exdigital.app.data.models

import com.google.gson.annotations.SerializedName

// ========== ÓRDENES ==========

// Modelo para un item individual de una orden
data class OrdenItemRequest(
    @SerializedName("productoId")
    val productoId: Long,

    @SerializedName("cantidad")
    val cantidad: Int,

    @SerializedName("precioUnitario")
    val precioUnitario: Double
)

// Modelo para item de orden en respuesta
data class OrdenItemResponse(
    val id: Long,

    @SerializedName("producto")
    val producto: ProductResponse,

    @SerializedName("cantidad")
    val cantidad: Int,

    @SerializedName("precioUnitario")
    val precioUnitario: Double
) {
    val subtotal: Double
        get() = cantidad * precioUnitario
}

// Modelo para recibir órdenes desde Supabase
data class OrderResponse(
    val id: Long,

    @SerializedName("usuario")
    val usuario: UsuarioResponse?,

    @SerializedName("items")
    val items: List<OrdenItemResponse>,

    @SerializedName("total")
    val total: Double,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("created_at")
    val createdAt: String? // ISO timestamp
)

// Modelo para crear una orden
data class CreateOrderRequest(
    @SerializedName("usuarioId")
    val usuarioId: Long,

    @SerializedName("items")
    val items: List<OrdenItemRequest>,

    @SerializedName("estado")
    val estado: String = "PAGADO"
)

// ========== PRODUCTOS ==========

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

