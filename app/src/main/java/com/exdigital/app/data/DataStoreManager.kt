package com.exdigital.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.exdigital.app.models.Cart
import com.exdigital.app.models.User
import com.exdigital.app.models.CartItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión para crear el DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "exdigital_prefs")

class DataStoreManager(private val context: Context) {

    private val gson = Gson()

    companion object {
        private val USER_KEY = stringPreferencesKey("user")
        private val CART_ITEMS_KEY = stringPreferencesKey("cart_items")
        private val IS_LOGGED_IN_KEY = stringPreferencesKey("is_logged_in")
    }

    // ========== USUARIO ==========

    suspend fun saveUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER_KEY] = gson.toJson(user)
            preferences[IS_LOGGED_IN_KEY] = "true"
        }
    }

    val userFlow: Flow<User?> = context.dataStore.data.map { preferences ->
        val userJson = preferences[USER_KEY]
        if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else null
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN_KEY] == "true"
    }

    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_KEY)
            preferences[IS_LOGGED_IN_KEY] = "false"
        }
    }

    // ========== CARRITO ==========

    suspend fun saveCart(cartItems: List<CartItem>) {
        context.dataStore.edit { preferences ->
            preferences[CART_ITEMS_KEY] = gson.toJson(cartItems)
        }
    }

    val cartFlow: Flow<List<CartItem>> = context.dataStore.data.map { preferences ->
        val cartJson = preferences[CART_ITEMS_KEY]
        if (cartJson != null) {
            val type = object : TypeToken<List<CartItem>>() {}.type
            gson.fromJson(cartJson, type) ?: emptyList()
        } else emptyList()
    }

    suspend fun addToCart(cartItem: CartItem) {
        context.dataStore.edit { preferences ->
            val currentCartJson = preferences[CART_ITEMS_KEY]
            val currentCart: MutableList<CartItem> = if (currentCartJson != null) {
                val type = object : TypeToken<List<CartItem>>() {}.type
                gson.fromJson(currentCartJson, type)
            } else {
                mutableListOf()
            }

            // Verificar si el producto ya existe en el carrito
            val existingItemIndex = currentCart.indexOfFirst {
                it.product.id == cartItem.product.id
            }

            if (existingItemIndex != -1) {
                // Si existe, actualizar cantidad
                val existingItem = currentCart[existingItemIndex]
                currentCart[existingItemIndex] = existingItem.copy(
                    quantity = existingItem.quantity + cartItem.quantity
                )
            } else {
                // Si no existe, agregar nuevo
                currentCart.add(cartItem)
            }

            preferences[CART_ITEMS_KEY] = gson.toJson(currentCart)
        }
    }

    suspend fun removeFromCart(productId: String) {
        context.dataStore.edit { preferences ->
            val currentCartJson = preferences[CART_ITEMS_KEY]
            if (currentCartJson != null) {
                val type = object : TypeToken<List<CartItem>>() {}.type
                val currentCart: MutableList<CartItem> = gson.fromJson(currentCartJson, type)
                currentCart.removeAll { it.product.id == productId }
                preferences[CART_ITEMS_KEY] = gson.toJson(currentCart)
            }
        }
    }

    suspend fun updateCartItemQuantity(productId: String, newQuantity: Int) {
        context.dataStore.edit { preferences ->
            val currentCartJson = preferences[CART_ITEMS_KEY]
            if (currentCartJson != null) {
                val type = object : TypeToken<List<CartItem>>() {}.type
                val currentCart: MutableList<CartItem> = gson.fromJson(currentCartJson, type)

                val itemIndex = currentCart.indexOfFirst { it.product.id == productId }
                if (itemIndex != -1) {
                    if (newQuantity > 0) {
                        currentCart[itemIndex] = currentCart[itemIndex].copy(quantity = newQuantity)
                    } else {
                        currentCart.removeAt(itemIndex)
                    }
                }

                preferences[CART_ITEMS_KEY] = gson.toJson(currentCart)
            }
        }
    }

    suspend fun clearCart() {
        context.dataStore.edit { preferences ->
            preferences[CART_ITEMS_KEY] = gson.toJson(emptyList<CartItem>())
        }
    }
}