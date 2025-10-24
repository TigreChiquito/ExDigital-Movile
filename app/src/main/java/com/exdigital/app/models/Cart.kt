package com.exdigital.app.models

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val subtotal: Double
        get() = product.price * quantity
}

data class Cart(
    val items: List<CartItem> = emptyList()
) {
    val total: Double
        get() = items.sumOf { it.subtotal }

    val itemCount: Int
        get() = items.sumOf { it.quantity }
}