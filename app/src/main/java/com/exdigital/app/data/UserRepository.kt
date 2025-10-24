package com.exdigital.app.data

import com.exdigital.app.models.User
import com.exdigital.app.models.UserRole

object UserRepository {

    // Usuarios administradores pre-registrados
    private val adminUsers = listOf(
        User(
            id = "admin_001",
            email = "admin@exdigital.com",
            name = "Administrador Principal",
            phone = "+56 9 1111 1111",
            role = UserRole.ADMIN
        ),
        User(
            id = "admin_002",
            email = "manager@exdigital.com",
            name = "Manager ExDigital",
            phone = "+56 9 2222 2222",
            role = UserRole.ADMIN
        )
    )

    // Contraseñas de administradores (en producción esto NUNCA va en el código)
    private val adminCredentials = mapOf(
        "admin@exdigital.com" to "admin123",
        "manager@exdigital.com" to "manager123"
    )

    // Verificar si es un usuario administrador
    fun isAdminUser(email: String, password: String): User? {
        val expectedPassword = adminCredentials[email]
        if (expectedPassword != null && expectedPassword == password) {
            return adminUsers.find { it.email == email }
        }
        return null
    }

    // Obtener usuario admin por email
    fun getAdminByEmail(email: String): User? {
        return adminUsers.find { it.email == email }
    }
}
