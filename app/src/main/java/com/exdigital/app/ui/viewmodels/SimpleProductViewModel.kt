package com.exdigital.app.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.exdigital.app.data.models.CreateProductRequest
import com.exdigital.app.data.models.ProductResponse
import com.exdigital.app.data.models.toProduct
import com.exdigital.app.data.remote.RetrofitClient
import com.exdigital.app.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SimpleProductViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        _isLoading.value = true

        RetrofitClient.instance.obtenerProductos()
            .enqueue(object : Callback<List<ProductResponse>> {
                override fun onResponse(
                    call: Call<List<ProductResponse>>,
                    response: Response<List<ProductResponse>>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val listaServidor = response.body() ?: emptyList()
                        _products.value = listaServidor.map { it.toProduct() }
                        Log.d("SimpleProductViewModel", "✅ Productos cargados: ${_products.value.size}")
                    } else {
                        Log.e("SimpleProductViewModel", "❌ Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<ProductResponse>>, t: Throwable) {
                    _isLoading.value = false
                    Log.e("SimpleProductViewModel", "💀 Error: ${t.message}")
                }
            })
    }

    fun addProduct(nombre: String, precio: Double, stock: Int, imagenUrl: String?) {
        _isLoading.value = true

        val request = CreateProductRequest(
            nombre = nombre,
            precio = precio,
            stock = stock,
            imagenUrl = imagenUrl
        )

        Log.d("SimpleProductViewModel", "📤 Enviando producto: nombre=$nombre, precio=$precio, stock=$stock")

        RetrofitClient.instance.crearProducto(request)
            .enqueue(object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        Log.d("SimpleProductViewModel", "✅ Producto creado: ${response.body()?.name}")
                        // Recargar productos
                        loadProducts()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("SimpleProductViewModel", "❌ Error al crear: ${response.code()}")
                        Log.e("SimpleProductViewModel", "❌ Error body: $errorBody")
                        Log.e("SimpleProductViewModel", "❌ Request: nombre=$nombre, precio=$precio, stock=$stock, url=$imagenUrl")
                    }
                }

                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.e("SimpleProductViewModel", "💀 Error de red: ${t.message}")
                    t.printStackTrace()
                }
            })
    }

    fun getProductById(id: String): Product? {
        return _products.value.find { it.id == id }
    }
}

