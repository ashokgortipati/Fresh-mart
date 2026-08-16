package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val fullName: String,
    val email: String,
    val phone: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class UserRegistrationRequest(
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val user: User,
    val token: String
)

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@Serializable
data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)

@Serializable
data class ErrorResponse(
    val message: String
)

@Serializable
data class SuccessResponse(
    val message: String
)
