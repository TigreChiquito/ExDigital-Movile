package com.exdigital.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exdigital.app.data.local.AppDatabase
import com.exdigital.app.data.repository.ProductRepository
import com.exdigital.app.models.Product
import com.exdigital.app.models.ProductCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository

    // Lista mutable de productos (empezamos con MockData)
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        val dao = db.productDao()
        repository = ProductRepository(dao)

        viewModelScope.launch {
            repository.populateIfEmpty()
        }

        viewModelScope.launch {
            repository.products.collect { list ->
                _products.value = list
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            repository.addProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.addProduct(product)
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }

    fun getProductById(id: String): Product? {
        return _products.value.find { it.id == id }
    }

    fun createNewProduct(
        name: String,
        description: String,
        price: Double,
        category: ProductCategory,
        stock: Int,
        brand: String
    ): Product {
        return Product(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            price = price,
            imageUrl = "https://via.placeholder.com/300x300/FF8A3D/FFFFFF?text=Producto",
            category = category,
            stock = stock,
            brand = brand,
            rating = 0.0
        )
    }
}