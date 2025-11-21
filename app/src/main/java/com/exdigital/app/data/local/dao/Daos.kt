package com.exdigital.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.exdigital.app.data.local.entities.CartItemEntity
import com.exdigital.app.data.local.entities.OrderEntity
import com.exdigital.app.data.local.entities.OrderItemEntity
import com.exdigital.app.data.local.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProduct(productId: String)

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductByIdOnce(productId: String): ProductEntity?
}

@Dao
interface PurchaseDao {

    // Cart operations
    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getCartItems(userId: String): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCartItem(cartItem: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItemById(id: Long)

    @Query("DELETE FROM cart_items WHERE userId = :userId AND productId = :productId")
    suspend fun deleteCartItem(userId: String, productId: String)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: String)

    // Orders
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY timestamp DESC")
    fun getOrdersByUser(userId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItems(orderId: Long): Flow<List<OrderItemEntity>>

    @Insert
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    suspend fun getCartItemsOnce(userId: String): List<CartItemEntity>

    @Transaction
    suspend fun checkout(userId: String): Long {
        val cartItems = getCartItemsOnce(userId)
        if (cartItems.isEmpty()) return -1

        val total = cartItems.sumOf { it.quantity * 1.0 }
        val orderId = insertOrder(
            OrderEntity(
                userId = userId,
                timestamp = System.currentTimeMillis(),
                total = total
            )
        )

        val orderItems = cartItems.map { cartItem ->
            OrderItemEntity(
                orderId = orderId,
                productId = cartItem.productId,
                quantity = cartItem.quantity,
                price = 0.0
            )
        }
        insertOrderItems(orderItems)
        clearCart(userId)
        return orderId
    }

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun getOrder(orderId: Long): Flow<OrderEntity>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsOnce(orderId: Long): List<OrderItemEntity>
}
