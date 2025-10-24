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
        // Primero verificar si es administrador
        val adminUser = UserRepository.isAdminUser(email, password)
        if (adminUser != null) {
            viewModelScope.launch {
                dataStoreManager.saveUser(adminUser)
            }
            return true
        }

        // Si no es admin, login normal de cliente
        if (email.isNotEmpty() && password.isNotEmpty()) {
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
        return false
    }

    fun register(email: String, password: String, name: String, phone: String): Boolean {
        // Registro siempre crea usuarios tipo CUSTOMER
        if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
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
        return false
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.logout()
        }
    }
}