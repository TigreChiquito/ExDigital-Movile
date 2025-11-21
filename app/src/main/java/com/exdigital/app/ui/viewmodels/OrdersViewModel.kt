package com.exdigital.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exdigital.app.data.local.AppDatabase
import com.exdigital.app.data.local.entities.OrderEntity
import com.exdigital.app.data.local.entities.OrderItemEntity
import com.exdigital.app.data.repository.PurchaseRepository
import com.exdigital.app.models.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrdersViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PurchaseRepository

    private val _orders = MutableStateFlow<List<OrderEntity>>(emptyList())
    val orders: StateFlow<List<OrderEntity>> = _orders.asStateFlow()

    private val _selectedOrderItems = MutableStateFlow<List<OrderItemEntity>>(emptyList())
    val selectedOrderItems: StateFlow<List<OrderItemEntity>> = _selectedOrderItems.asStateFlow()

    private val _selectedOrder = MutableStateFlow<Pair<OrderEntity, List<CartItem>>?>(null)
    val selectedOrder: StateFlow<Pair<OrderEntity, List<CartItem>>?> = _selectedOrder.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = PurchaseRepository(db.purchaseDao(), db.productDao())
    }

    fun loadUserOrders(userId: String) {
        viewModelScope.launch {
            repository.getOrdersByUser(userId).collect { list ->
                _orders.value = list
            }
        }
    }

    fun loadAllOrders() {
        viewModelScope.launch {
            repository.getAllOrders().collect { list ->
                _orders.value = list
            }
        }
    }

    fun loadOrderItems(orderId: Long) {
        viewModelScope.launch {
            repository.getOrderItems(orderId).collect { list ->
                _selectedOrderItems.value = list
            }
        }
    }

    fun loadOrderDetail(orderId: Long) {
        viewModelScope.launch {
            repository.getOrderWithItems(orderId).collect { pair ->
                _selectedOrder.value = pair
            }
        }
    }
}
