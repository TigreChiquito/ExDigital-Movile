package com.exdigital.app.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.exdigital.app.models.Cart
import com.exdigital.app.models.CartItem
import com.exdigital.app.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartViewModel : ViewModel() {

    // Versión simplificada en memoria para desarrollo educativo
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _cart = MutableStateFlow(Cart())
    val cart: StateFlow<Cart> = _cart.asStateFlow()

    fun addToCart(product: Product, quantity: Int = 1) {
        Log.d("CartViewModel", "🛒 addToCart llamado: ${product.name}, cantidad: $quantity")

        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.find { it.product.id == product.id }

        if (existingItem != null) {
            // Actualizar cantidad
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
            currentItems[currentItems.indexOf(existingItem)] = updatedItem
            Log.d("CartViewModel", "✅ Producto actualizado: ${product.name}, nueva cantidad: ${updatedItem.quantity}")
        } else {
            // Agregar nuevo item
            currentItems.add(CartItem(product, quantity))
            Log.d("CartViewModel", "✅ Producto agregado: ${product.name}, cantidad: $quantity")
        }

        _cartItems.value = currentItems
        _cart.value = Cart(currentItems)

        Log.d("CartViewModel", "📊 Total items en carrito: ${_cart.value.itemCount}")
        Log.d("CartViewModel", "💰 Total carrito: $${_cart.value.total}")
    }

    fun removeFromCart(productId: String) {
        val currentItems = _cartItems.value.toMutableList()
        currentItems.removeAll { it.product.id == productId }
        _cartItems.value = currentItems
        _cart.value = Cart(currentItems)
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(productId)
            return
        }

        val currentItems = _cartItems.value.toMutableList()
        val itemIndex = currentItems.indexOfFirst { it.product.id == productId }

        if (itemIndex != -1) {
            currentItems[itemIndex] = currentItems[itemIndex].copy(quantity = newQuantity)
            _cartItems.value = currentItems
            _cart.value = Cart(currentItems)
        }
    }

    fun clearCart() {
        Log.d("CartViewModel", "🗑️ Carrito limpiado")
        _cartItems.value = emptyList()
        _cart.value = Cart()
    }

    fun checkout(userId: String, ordersViewModel: OrdersViewModel, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        val items = _cartItems.value
        val total = _cart.value.total

        if (items.isEmpty()) {
            Log.e("CartViewModel", "⚠️ Checkout cancelado: carrito vacío")
            onError("El carrito está vacío")
            return
        }

        // Convertir userId String a Long
        val userIdLong = userId.toLongOrNull()
        if (userIdLong == null) {
            Log.e("CartViewModel", "❌ Error: userId no es un número válido: $userId")
            onError("Error: Usuario no válido")
            return
        }

        Log.d("CartViewModel", "✅ Checkout iniciado: ${items.size} items, total: $$total, usuario: $userIdLong")

        ordersViewModel.addOrder(
            usuarioId = userIdLong,
            items = items,
            total = total,
            onSuccess = {
                Log.d("CartViewModel", "🗑️ Orden creada exitosamente - Limpiando carrito")
                clearCart()
                onSuccess()
            },
            onError = { error ->
                Log.e("CartViewModel", "❌ Error al crear orden: $error")
                onError(error)
            }
        )
    }
}