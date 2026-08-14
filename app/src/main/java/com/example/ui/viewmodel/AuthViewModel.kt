package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.model.UserProfile
import com.example.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthUiState(
    val isLoggedIn: Boolean = true, // Logged in by default with sample user
    val isLoginMode: Boolean = true, // Toggle between Login & Signup
    val emailInput: String = "ashokgortipati3@gmail.com",
    val passwordInput: String = "••••••••",
    val nameInput: String = "Ashok Gortipati",
    val phoneInput: String = "+1 (555) 234-5678",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showForgotPasswordDialog: Boolean = false,
    val forgotPasswordEmail: String = ""
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ShopRepository(application)
    val userProfile: StateFlow<UserProfile> = repository.userProfile

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun toggleAuthMode() {
        _uiState.value = _uiState.value.copy(
            isLoginMode = !_uiState.value.isLoginMode,
            errorMessage = null,
            successMessage = null
        )
    }

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(emailInput = email, errorMessage = null)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(passwordInput = password, errorMessage = null)
    }

    fun onNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(nameInput = name, errorMessage = null)
    }

    fun onPhoneChanged(phone: String) {
        _uiState.value = _uiState.value.copy(phoneInput = phone, errorMessage = null)
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
        if (email.isNotBlank() && email.contains("@")) {
            _uiState.value = _uiState.value.copy(
                showForgotPasswordDialog = false,
                successMessage = "Password reset instructions sent to $email"
            )
        } else {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid email address.")
        }
    }

    fun loginWithEmail() {
        val s = _uiState.value
        if (s.emailInput.isBlank() || s.passwordInput.isBlank()) {
            _uiState.value = s.copy(errorMessage = "Please enter your email and password.")
            return
        }
        _uiState.value = s.copy(isLoading = true, errorMessage = null)
        repository.updateUserProfile(s.nameInput.ifBlank { "Ashok Gortipati" }, s.emailInput, s.phoneInput)
        _uiState.value = s.copy(isLoading = false, isLoggedIn = true, successMessage = "Welcome back to FreshMart!")
    }

    fun signUpWithEmail() {
        val s = _uiState.value
        if (s.emailInput.isBlank() || s.passwordInput.isBlank() || s.nameInput.isBlank()) {
            _uiState.value = s.copy(errorMessage = "Please fill in all fields.")
            return
        }
        _uiState.value = s.copy(isLoading = true, errorMessage = null)
        repository.updateUserProfile(s.nameInput, s.emailInput, s.phoneInput)
        _uiState.value = s.copy(isLoading = false, isLoggedIn = true, successMessage = "Account created successfully!")
    }

    fun loginWithGoogle() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        repository.updateUserProfile("Ashok Gortipati", "ashokgortipati3@gmail.com", "+1 (555) 234-5678")
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isLoggedIn = true,
            emailInput = "ashokgortipati3@gmail.com",
            nameInput = "Ashok Gortipati",
            successMessage = "Signed in with Google as Ashok Gortipati"
        )
    }

    fun logout() {
        _uiState.value = _uiState.value.copy(isLoggedIn = false, successMessage = "Logged out successfully")
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
