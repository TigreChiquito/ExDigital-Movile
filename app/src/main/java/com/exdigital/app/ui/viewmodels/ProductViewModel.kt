package com.exdigital.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.exdigital.app.models.Product
import com.exdigital.app.models.ProductCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ProductViewModel : ViewModel() {

    // Lista mutable de productos (empezamos con MockData)
    private val _products = MutableStateFlow<List<Product>>(
        com.exdigital.app.data.MockData.products.toMutableList()
    )
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    fun addProduct(product: Product) {
        val currentList = _products.value.toMutableList()
        currentList.add(product)
        _products.value = currentList
    }

    fun updateProduct(product: Product) {
        val currentList = _products.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == product.id }
        if (index != -1) {
            currentList[index] = product
            _products.value = currentList
        }
    }

    fun deleteProduct(productId: String) {
        val currentList = _products.value.toMutableList()
        currentList.removeAll { it.id == productId }
        _products.value = currentList
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