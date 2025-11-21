package com.exdigital.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exdigital.app.data.DataStoreManager
import com.exdigital.app.data.UserRepository
import com.exdigital.app.models.User
import com.exdigital.app.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    // Validaciones básicas educativas
    private fun isValidEmail(email: String): Boolean {
        val allowedDomains = listOf("@duoc.cl", "@duocuc.cl", "@gmail.com")
        return email.contains("@") && allowedDomains.any { email.endsWith(it) }
    }

    private fun isValidPassword(password: String): Boolean {
        if (password.length !in 5..9) return false
        if (!password.any { it.isUpperCase() }) return false
        return true
    }

    // En esta versión educativa NO persistimos lista de usuarios; solo el usuario actual en DataStore.

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            dataStoreManager.userFlow.collect { user ->
                _currentUser.value = user
                _isAdmin.value = user?.role == UserRole.ADMIN
            }
        }

        viewModelScope.launch {
            dataStoreManager.isLoggedInFlow.collect { isLoggedIn ->
                _isLoggedIn.value = isLoggedIn
            }
        }
    }

    fun login(email: String, password: String): Boolean {
        // Admin
        val adminUser = UserRepository.isAdminUser(email, password)
        if (adminUser != null) {
            viewModelScope.launch {
                dataStoreManager.saveUser(adminUser)
            }
            return true
        }

        // Validaciones de formato
        if (!isValidEmail(email) || !isValidPassword(password)) {
            return false
        }

        // Como no tenemos BD de usuarios aún, aceptamos login educativo:
        val user = User(
            id = UUID.randomUUID().toString(),
            email = email,
            name = email.substringBefore("@"),
            role = UserRole.CUSTOMER
        )
        viewModelScope.launch {
            dataStoreManager.saveUser(user)
        }
        return true
    }

    fun register(email: String, password: String, name: String, phone: String): Boolean {
        // Validaciones de campos
        if (email.isBlank() || password.isBlank() || name.isBlank()) return false
        if (!isValidEmail(email) || !isValidPassword(password)) return false

        // En una app real aquí comprobaríamos si el usuario ya existe en BD.

        val user = User(
            id = UUID.randomUUID().toString(),
            email = email,
            name = name,
            phone = phone,
            role = UserRole.CUSTOMER
        )

        viewModelScope.launch {
            dataStoreManager.saveUser(user)
        }
        return true
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.logout()
        }
    }
}