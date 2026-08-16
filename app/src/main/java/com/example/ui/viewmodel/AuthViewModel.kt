package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.AuthRepository
import com.example.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val isLoginMode: Boolean = true,
    val emailInput: String = "",
    val passwordInput: String = "",
    val confirmPasswordInput: String = "",
    val nameInput: String = "",
    val phoneInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
    val successMessage: String? = null,
    val showForgotPasswordDialog: Boolean = false,
    val forgotPasswordEmail: String = ""
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val shopRepository = ShopRepository(application)
    private val authRepository = AuthRepository(application)
    val userProfile: StateFlow<UserProfile> = shopRepository.userProfile

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (authRepository.isLoggedIn()) {
                val result = authRepository.getMe()
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    if (user != null) {
                        shopRepository.updateUserProfile(user.fullName, user.email, user.phone)
                        _uiState.value = _uiState.value.copy(isLoggedIn = true)
                    }
                }
            }
        }
    }

    fun toggleAuthMode() {
        _uiState.value = _uiState.value.copy(
            isLoginMode = !_uiState.value.isLoginMode,
            errorMessage = null,
            fieldErrors = emptyMap(),
            successMessage = null
        )
    }

    fun onEmailChanged(email: String) = updateField { copy(emailInput = email) }
    fun onPasswordChanged(password: String) = updateField { copy(passwordInput = password) }
    fun onConfirmPasswordChanged(password: String) = updateField { copy(confirmPasswordInput = password) }
    fun onNameChanged(name: String) = updateField { copy(nameInput = name) }
    fun onPhoneChanged(phone: String) = updateField { copy(phoneInput = phone) }

    private fun updateField(update: AuthUiState.() -> AuthUiState) {
        _uiState.value = _uiState.value.update().copy(errorMessage = null, fieldErrors = emptyMap())
    }

    fun openForgotPassword(open: Boolean) {
        _uiState.value = _uiState.value.copy(
            showForgotPasswordDialog = open,
            forgotPasswordEmail = _uiState.value.emailInput
        )
    }

    fun setForgotPasswordEmail(email: String) {
        _uiState.value = _uiState.value.copy(forgotPasswordEmail = email)
    }

    fun sendPasswordReset() {
        val email = _uiState.value.forgotPasswordEmail
        if (email.isBlank() || !email.contains("@")) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid email address.")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = authRepository.forgotPassword(email)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showForgotPasswordDialog = false,
                    successMessage = "Password reset instructions sent to $email"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to send reset email"
                )
            }
        }
    }

    fun loginWithEmail() {
        val s = _uiState.value
        if (s.emailInput.isBlank() || s.passwordInput.isBlank()) {
            _uiState.value = s.copy(errorMessage = "Please enter your email and password.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.login(LoginRequest(s.emailInput, s.passwordInput))
            
            if (result.isSuccess) {
                val user = result.getOrNull()?.user
                if (user != null) {
                    shopRepository.updateUserProfile(user.fullName, user.email, user.phone)
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false, 
                    isLoggedIn = true, 
                    successMessage = "Welcome back to FreshMart!"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, 
                    errorMessage = result.exceptionOrNull()?.message ?: "Login failed"
                )
            }
        }
    }

    fun signUpWithEmail() {
        val s = _uiState.value
        val errors = validateSignUp(s)
        if (errors.isNotEmpty()) {
            _uiState.value = s.copy(fieldErrors = errors)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.register(
                UserRegistrationRequest(s.nameInput, s.emailInput, s.phoneInput, s.passwordInput)
            )

            if (result.isSuccess) {
                val user = result.getOrNull()?.user
                if (user != null) {
                    shopRepository.updateUserProfile(user.fullName, user.email, user.phone)
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false, 
                    isLoggedIn = true, 
                    successMessage = "Account created successfully!"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, 
                    errorMessage = result.exceptionOrNull()?.message ?: "Registration failed"
                )
            }
        }
    }

    private fun validateSignUp(s: AuthUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        
        if (s.nameInput.isBlank()) errors["name"] = "Full Name is required"
        
        if (s.emailInput.isBlank()) errors["email"] = "Email is required"
        else if (!s.emailInput.contains("@")) errors["email"] = "Invalid email format"
        
        if (s.phoneInput.isBlank()) errors["phone"] = "Phone Number is required"
        else if (s.phoneInput.length != 10) errors["phone"] = "Enter a valid 10-digit number"
        
        if (s.passwordInput.length < 8) {
            errors["password"] = "Password must be at least 8 characters"
        } else if (!s.passwordInput.any { it.isUpperCase() } || 
                   !s.passwordInput.any { it.isLowerCase() } || 
                   !s.passwordInput.any { it.isDigit() } ||
                   !s.passwordInput.any { !it.isLetterOrDigit() }) {
            errors["password"] = "Must contain uppercase, lowercase, number and special char"
        }
        
        if (s.confirmPasswordInput != s.passwordInput) {
            errors["confirmPassword"] = "Passwords do not match"
        }
        
        return errors
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState(isLoggedIn = false, successMessage = "Logged out successfully")
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
