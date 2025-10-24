package com.exdigital.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exdigital.app.data.DataStoreManager
import com.exdigital.app.models.Cart
import com.exdigital.app.models.CartItem
import com.exdigital.app.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application)

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _cart = MutableStateFlow(Cart())
    val cart: StateFlow<Cart> = _cart.asStateFlow()

    init {
        loadCart()
    }

    private fun loadCart() {
        viewModelScope.launch {
            dataStoreManager.cartFlow.collect { items ->
                _cartItems.value = items
                _cart.value = Cart(items)
            }
        }
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            val cartItem = CartItem(product, quantity)
            dataStoreManager.addToCart(cartItem)
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            dataStoreManager.removeFromCart(productId)
        }
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        viewModelScope.launch {
            dataStoreManager.updateCartItemQuantity(productId, newQuantity)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            dataStoreManager.clearCart()
        }
    }
}