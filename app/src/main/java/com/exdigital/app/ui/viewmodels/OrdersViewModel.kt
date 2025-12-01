package com.exdigital.app.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exdigital.app.data.models.CreateOrderRequest
import com.exdigital.app.data.models.OrderResponse
import com.exdigital.app.data.remote.RetrofitClient
import com.exdigital.app.models.CartItem
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

data class Order(
    val id: Long,
    val userId: String,
    val items: List<CartItem>,
    val total: Double,
    val timestamp: Long,
    val status: String = "PAGADO"
)

class OrdersViewModel : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val gson = Gson()

    fun addOrder(userId: String, items: List<CartItem>, total: Double) {
        _isLoading.value = true

        // Serializar items a JSON
        val itemsJson = gson.toJson(items)

        val request = CreateOrderRequest(
            usuarioId = userId,
            total = total,
            estado = "PAGADO",
            items = itemsJson
        )

        RetrofitClient.instance.crearOrden(request).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    Log.d("OrdersViewModel", "✅ Orden creada exitosamente: ${response.body()?.id}")
                    // Recargar órdenes
                    loadAllOrders()
                } else {
                    Log.e("OrdersViewModel", "❌ Error al crear orden: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("OrdersViewModel", "💀 Error de red al crear orden: ${t.message}")
            }
        })
    }

    fun loadUserOrders(userId: String) {
        loadAllOrders(filterByUserId = userId)
    }

    fun loadAllOrders(filterByUserId: String? = null) {
        _isLoading.value = true

        RetrofitClient.instance.obtenerOrdenes().enqueue(object : Callback<List<OrderResponse>> {
            override fun onResponse(
                call: Call<List<OrderResponse>>,
                response: Response<List<OrderResponse>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val ordersFromApi = response.body() ?: emptyList()
                    Log.d("OrdersViewModel", "✅ Órdenes cargadas: ${ordersFromApi.size}")

                    // Convertir OrderResponse a Order
                    var orders = ordersFromApi.mapNotNull { orderResponse ->
                        try {
                            // Parsear items JSON
                            val items = if (!orderResponse.items.isNullOrEmpty()) {
                                gson.fromJson(
                                    orderResponse.items,
                                    Array<CartItem>::class.java
                                ).toList()
                            } else {
                                emptyList()
                            }

                            // Parsear timestamp
                            val timestamp = try {
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                    .parse(orderResponse.createdAt)?.time ?: System.currentTimeMillis()
                            } catch (e: Exception) {
                                System.currentTimeMillis()
                            }

                            Order(
                                id = orderResponse.id,
                                userId = orderResponse.usuarioId,
                                items = items,
                                total = orderResponse.total,
                                timestamp = timestamp,
                                status = orderResponse.estado
                            )
                        } catch (e: Exception) {
                            Log.e("OrdersViewModel", "Error parseando orden: ${e.message}")
                            null
                        }
                    }

                    // Filtrar por usuario si es necesario
                    if (filterByUserId != null) {
                        orders = orders.filter { it.userId == filterByUserId }
                    }

                    _orders.value = orders
                } else {
                    Log.e("OrdersViewModel", "❌ Error al cargar órdenes: ${response.code()}")
                    _orders.value = emptyList()
                }
            }

            override fun onFailure(call: Call<List<OrderResponse>>, t: Throwable) {
                _isLoading.value = false
                Log.e("OrdersViewModel", "💀 Error de red al cargar órdenes: ${t.message}")
                _orders.value = emptyList()
            }
        })
    }

    fun selectOrder(order: Order) {
        _selectedOrder.value = order
    }

    fun clearSelectedOrder() {
        _selectedOrder.value = null
    }
}
