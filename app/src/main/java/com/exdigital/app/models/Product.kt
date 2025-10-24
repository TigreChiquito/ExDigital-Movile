package com.exdigital.app.models

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: ProductCategory,
    val stock: Int,
    val brand: String,
    val rating: Double = 0.0
)

enum class ProductCategory {
    MOUSE,
    KEYBOARD,
    HEADSET,
    CONTROLLER,
    MONITOR,
    MICROPHONE,
    OTHER
}

fun ProductCategory.displayName(): String {
    return when(this) {
        ProductCategory.MOUSE -> "Mouse"
        ProductCategory.KEYBOARD -> "Teclado"
        ProductCategory.HEADSET -> "Audífonos"
        ProductCategory.CONTROLLER -> "Control"
        ProductCategory.MONITOR -> "Monitor"
        ProductCategory.MICROPHONE -> "Micrófono"
        ProductCategory.OTHER -> "Otros"
    }
}