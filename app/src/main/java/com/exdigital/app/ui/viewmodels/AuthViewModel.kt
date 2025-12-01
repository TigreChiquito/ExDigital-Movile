package com.exdigital.app.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exdigital.app.data.DataStoreManager
import com.exdigital.app.data.models.LoginRequest
import com.exdigital.app.data.models.LoginResponse
import com.exdigital.app.data.models.RegistroRequest
import com.exdigital.app.data.models.UsuarioResponse
import com.exdigital.app.data.remote.RetrofitClient
import com.exdigital.app.models.User
import com.exdigital.app.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Validaciones básicas
    private fun isValidEmail(email: String): Boolean {
        val allowedDomains = listOf("@duoc.cl", "@duocuc.cl", "@gmail.com")
        return email.contains("@") && allowedDomains.any { email.endsWith(it) }
    }

    private fun isValidPassword(password: String): Boolean {
        if (password.length !in 5..9) return false
        if (!password.any { it.isUpperCase() }) return false
        return true
    }

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

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        _isLoading.value = true
        _authError.value = null

        val request = LoginRequest(email = email, password = password)

        Log.d("AuthViewModel", "📤 Intentando login con email: $email")
        Log.d("AuthViewModel", "📤 URL: ${RetrofitClient.instance.loginUsuario(request).request().url}")

        RetrofitClient.instance.loginUsuario(request)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    _isLoading.value = false

                    Log.d("AuthViewModel", "📥 Response code: ${response.code()}")

                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        Log.d("AuthViewModel", "📥 Response body: success=${loginResponse?.success}, usuario=${loginResponse?.usuario?.email}")

                        if (loginResponse?.success == true && loginResponse.usuario != null) {
                            val usuarioResponse = loginResponse.usuario
                            val user = User(
                                id = usuarioResponse.id.toString(),
                                email = usuarioResponse.email,
                                name = usuarioResponse.nombre,
                                phone = usuarioResponse.telefono ?: "",
                                role = if (usuarioResponse.rol == "ADMIN") UserRole.ADMIN else UserRole.CUSTOMER
                            )

                            viewModelScope.launch {
                                dataStoreManager.saveUser(user)
                            }

                            Log.d("AuthViewModel", "✅ Login exitoso: ${user.email}, rol: ${user.role}")
                            onResult(true, "Sesión iniciada correctamente")
                        } else {
                            val errorMsg = loginResponse?.message ?: "Credenciales inválidas"
                            _authError.value = errorMsg
                            Log.e("AuthViewModel", "❌ Login fallido: $errorMsg")
                            onResult(false, errorMsg)
                        }
                    } else {
                        // Error HTTP (401, 400, 500, etc.)
                        val errorBody = response.errorBody()?.string()
                        val errorMsg = when (response.code()) {
                            401 -> "Credenciales incorrectas. Verifica tu email y contraseña."
                            404 -> "Servicio de login no disponible. Verifica que el servidor esté corriendo."
                            500 -> "Error interno del servidor."
                            else -> "Error del servidor: ${response.code()}"
                        }

                        _authError.value = errorMsg
                        Log.e("AuthViewModel", "❌ Error HTTP: ${response.code()}")
                        Log.e("AuthViewModel", "❌ Error body: $errorBody")
                        Log.e("AuthViewModel", "❌ Email enviado: $email")

                        onResult(false, errorMsg)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    _isLoading.value = false
                    val errorMsg = "Error de conexión: ${t.message}"
                    _authError.value = errorMsg
                    Log.e("AuthViewModel", "💀 Error de red: ${t.message}")
                    t.printStackTrace()
                    onResult(false, "No se pudo conectar al servidor. ¿Está corriendo en puerto 8081?")
                }
            })
    }

    fun register(email: String, password: String, name: String, phone: String, onResult: (Boolean, String?) -> Unit) {
        // Validaciones locales primero
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            onResult(false, "Todos los campos son obligatorios")
            return
        }

        if (!isValidEmail(email)) {
            onResult(false, "Email debe ser @duoc.cl, @duocuc.cl o @gmail.com")
            return
        }

        if (!isValidPassword(password)) {
            onResult(false, "Contraseña: 5-9 caracteres, al menos 1 mayúscula")
            return
        }

        _isLoading.value = true
        _authError.value = null

        val request = RegistroRequest(
            email = email,
            nombre = name,
            password = password,
            telefono = phone.ifEmpty { null },
            direccion = null
        )

        RetrofitClient.instance.registrarUsuario(request)
            .enqueue(object : Callback<UsuarioResponse> {
                override fun onResponse(
                    call: Call<UsuarioResponse>,
                    response: Response<UsuarioResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val usuarioResponse = response.body()
                        if (usuarioResponse != null) {
                            val user = User(
                                id = usuarioResponse.id.toString(),
                                email = usuarioResponse.email,
                                name = usuarioResponse.nombre,
                                phone = usuarioResponse.telefono ?: "",
                                role = UserRole.CUSTOMER
                            )

                            viewModelScope.launch {
                                dataStoreManager.saveUser(user)
                            }

                            Log.d("AuthViewModel", "✅ Registro exitoso: ${user.email}")
                            onResult(true, "Cuenta creada correctamente")
                        } else {
                            onResult(false, "Error al procesar respuesta")
                        }
                    } else {
                        val errorMsg = when (response.code()) {
                            400 -> "El email ya está registrado"
                            else -> "Error del servidor: ${response.code()}"
                        }
                        _authError.value = errorMsg
                        Log.e("AuthViewModel", "❌ Error HTTP: ${response.code()}")
                        onResult(false, errorMsg)
                    }
                }

                override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                    _isLoading.value = false
                    val errorMsg = "Error de conexión: ${t.message}"
                    _authError.value = errorMsg
                    Log.e("AuthViewModel", "💀 Error de red: ${t.message}")
                    onResult(false, errorMsg)
                }
            })
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.logout()
            _authError.value = null
        }
    }

    fun clearError() {
        _authError.value = null
    }
}