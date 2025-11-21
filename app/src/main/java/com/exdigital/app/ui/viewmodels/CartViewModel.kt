package com.exdigital.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exdigital.app.data.local.AppDatabase
import com.exdigital.app.data.repository.PurchaseRepository
import com.exdigital.app.models.Cart
import com.exdigital.app.models.CartItem
import com.exdigital.app.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val purchaseRepository: PurchaseRepository

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _cart = MutableStateFlow(Cart())
    val cart: StateFlow<Cart> = _cart.asStateFlow()

    // Por simplicidad, guardamos el userId actual aquí.
    private val _userId = MutableStateFlow<String?>(null)

    init {
        val db = AppDatabase.getInstance(application)
        purchaseRepository = PurchaseRepository(db.purchaseDao(), db.productDao())
    }

    fun setUserId(userId: String) {
        _userId.value = userId
        viewModelScope.launch {
            purchaseRepository.getCartItems(userId).collect { items ->
                _cartItems.value = items
                _cart.value = Cart(items)
            }
        }
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        val userId = _userId.value ?: return
        viewModelScope.launch {
            purchaseRepository.addOrUpdateCartItem(userId, product, quantity)
        }
    }

    fun removeFromCart(productId: String) {
        val userId = _userId.value ?: return
        viewModelScope.launch {
            purchaseRepository.removeFromCart(userId, productId)
        }
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        val userId = _userId.value ?: return
        val currentItem = _cartItems.value.find { it.product.id == productId } ?: return
        viewModelScope.launch {
            purchaseRepository.addOrUpdateCartItem(userId, currentItem.product, newQuantity)
        }
    }

    fun clearCart() {
        val userId = _userId.value ?: return
        viewModelScope.launch {
            purchaseRepository.clearCart(userId)
        }
    }

    fun checkout() {
        val userId = _userId.value ?: return
        viewModelScope.launch {
            purchaseRepository.checkout(userId)
        }
    }
}