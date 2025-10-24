package com.exdigital.app.models

data class User(
    val id: String,
    val email: String,
    val name: String,
    val phone: String = "",
    val address: String = "",
    val role: UserRole = UserRole.CUSTOMER
)

enum class UserRole {
    ADMIN,
    CUSTOMER
}