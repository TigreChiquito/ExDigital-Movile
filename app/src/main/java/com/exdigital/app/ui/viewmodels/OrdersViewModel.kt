package com.exdigital.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.exdigital.app.models.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Order(
    val id: Long,
    val userId: String,
    val items: List<CartItem>,
    val total: Double,
    val timestamp: Long,
    val status: String = "PAGADO"
)

class OrdersViewModel : ViewModel() {

    // Versión simplificada en memoria para desarrollo educativo
    private val _allOrders = MutableStateFlow<List<Order>>(emptyList())

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()

    fun addOrder(userId: String, items: List<CartItem>, total: Double) {
        val newOrder = Order(
            id = System.currentTimeMillis(),
            userId = userId,
            items = items.toList(),
            total = total,
            timestamp = System.currentTimeMillis()
        )

        _allOrders.value = _allOrders.value + newOrder
    }

    fun loadUserOrders(userId: String) {
        _orders.value = _allOrders.value.filter { it.userId == userId }
    }

    fun loadAllOrders() {
        _orders.value = _allOrders.value
    }

    fun selectOrder(order: Order) {
        _selectedOrder.value = order
    }

    fun clearSelectedOrder() {
        _selectedOrder.value = null
    }
}
