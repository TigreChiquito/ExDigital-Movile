package com.exdigital.app.data.repository

import com.exdigital.app.data.MockData
import com.exdigital.app.data.local.dao.ProductDao
import com.exdigital.app.data.local.dao.PurchaseDao
import com.exdigital.app.data.local.entities.CartItemEntity
import com.exdigital.app.data.local.entities.OrderEntity
import com.exdigital.app.data.local.entities.OrderItemEntity
import com.exdigital.app.data.local.entities.ProductEntity
import com.exdigital.app.models.CartItem
import com.exdigital.app.models.Product
import com.exdigital.app.models.ProductCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepository(private val productDao: ProductDao) {

    val products: Flow<List<Product>> = productDao.getAllProducts().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun populateIfEmpty() {
        // Simple: always insert mock data on first call if table is empty
        // Could be improved by checking count
        val mockEntities = MockData.products.map { it.toEntity() }
        productDao.insertProducts(mockEntities)
    }

    suspend fun addProduct(product: Product) {
        productDao.insertProduct(product.toEntity())
    }

    suspend fun deleteProduct(id: String) {
        productDao.deleteProduct(id)
    }
}

class PurchaseRepository(private val purchaseDao: PurchaseDao, private val productDao: ProductDao) {

    fun getCartItems(userId: String): Flow<List<CartItem>> =
        purchaseDao.getCartItems(userId).map { list ->
            list.map { it.toDomain() }
        }

    suspend fun addOrUpdateCartItem(userId: String, product: Product, quantity: Int) {
        val entity = CartItemEntity(
            userId = userId,
            productId = product.id,
            quantity = quantity
        )
        purchaseDao.upsertCartItem(entity)
    }

    suspend fun removeFromCart(userId: String, productId: String) {
        purchaseDao.deleteCartItem(userId, productId)
    }

    suspend fun clearCart(userId: String) {
        purchaseDao.clearCart(userId)
    }

    suspend fun checkout(userId: String): Long = purchaseDao.checkout(userId)

    fun getOrdersByUser(userId: String): Flow<List<OrderEntity>> = purchaseDao.getOrdersByUser(userId)

    fun getAllOrders(): Flow<List<OrderEntity>> = purchaseDao.getAllOrders()

    fun getOrderItems(orderId: Long): Flow<List<OrderItemEntity>> = purchaseDao.getOrderItems(orderId)

    fun getOrderWithItems(orderId: Long): Flow<Pair<OrderEntity, List<CartItem>>> =
        purchaseDao.getOrder(orderId).map { order ->
            val items = purchaseDao.getOrderItemsOnce(order.id).map { item ->
                val productEntity = productDao.getProductByIdOnce(item.productId)
                val product = productEntity?.let { entity ->
                    Product(
                        id = entity.id,
                        name = entity.name,
                        description = entity.description,
                        price = item.price.takeIf { it > 0.0 } ?: entity.price,
                        imageUrl = entity.imageUrl,
                        category = ProductCategory.valueOf(entity.category),
                        stock = entity.stock,
                        brand = entity.brand,
                        rating = entity.rating
                    )
                } ?: Product(
                    id = item.productId,
                    name = "Producto eliminado",
                    description = "",
                    price = item.price,
                    imageUrl = "",
                    category = ProductCategory.OTHER,
                    stock = 0,
                    brand = "",
                    rating = 0.0
                )
                CartItem(product = product, quantity = item.quantity)
            }
            order to items
        }
}

// Mapping extensions

private fun ProductEntity.toDomain(): Product = Product(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    category = com.exdigital.app.models.ProductCategory.valueOf(category),
    stock = stock,
    brand = brand,
    rating = rating
)

private fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    category = category.name,
    stock = stock,
    brand = brand,
    rating = rating
)

private fun CartItemEntity.toDomain(): CartItem = CartItem(
    product = Product(
        id = productId,
        name = "",
        description = "",
        price = 0.0,
        imageUrl = "",
        category = com.exdigital.app.models.ProductCategory.OTHER,
        stock = 0,
        brand = "",
        rating = 0.0
    ),
    quantity = quantity
)
