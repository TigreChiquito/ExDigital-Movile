package com.exdigital.app.data.models

import com.google.gson.annotations.SerializedName
import com.exdigital.app.models.Product
import com.exdigital.app.models.ProductCategory

data class ProductResponse(
    val id: Long,

    @SerializedName("nombre")
    val name: String,

    @SerializedName("precio")
    val price: Double,

    val stock: Int,

    @SerializedName("imagenUrl")
    val imageUrl: String?
)

fun ProductResponse.toProduct(): Product {
    return Product(
        id = this.id.toString(),
        name = this.name,
        description = "Producto importado",
        price = this.price,
        imageUrl = this.imageUrl ?: "",
        category = ProductCategory.OTHER,
        stock = this.stock,
        brand = "Genérico",
        rating = 5.0
    )
}