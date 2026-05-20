package com.example.financeapp.ui.screens.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onNameChange(newValue: String) {
        _uiState.update {
            it.copy(
                name = newValue,
                nameError = false,
                authErrorMessage = "",
                isRegisterSuccessful = false
            )
        }
    }

    fun onEmailChange(newValue: String) {
        _uiState.update {
            it.copy(
                email = newValue,
                emailError = false,
                authErrorMessage = "",
                isLoginSuccessful = false,
                isRegisterSuccessful = false
            )
        }
    }

    fun onPasswordChange(newValue: String) {
        _uiState.update {
            it.copy(
                password = newValue,
                passwordError = false,
                authErrorMessage = "",
                isLoginSuccessful = false,
                isRegisterSuccessful = false
            )
        }
    }

    fun onConfirmPasswordChange(newValue: String) {
        _uiState.update {
            it.copy(
                confirmPassword = newValue,
                confirmPasswordError = false,
                authErrorMessage = "",
                isRegisterSuccessful = false
            )
        }
    }

    fun onLoginClick() {
        val currentState = _uiState.value

        val isEmailInvalid = currentState.email.isBlank() ||
                !Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()

        val isPasswordInvalid = currentState.password.isBlank() ||
                currentState.password.length < 6

        if (isEmailInvalid || isPasswordInvalid) {
            _uiState.update {
                it.copy(
                    emailError = isEmailInvalid,
                    passwordError = isPasswordInvalid,
                    authErrorMessage = "",
                    isLoginSuccessful = false
                )
            }
            return
        }

        viewModelScope.launch {
            val loginResult = authRepository.loginUser(
                email = currentState.email,
                password = currentState.password
            )

            _uiState.update {
                it.copy(
                    emailError = false,
                    passwordError = false,
                    authErrorMessage = if (loginResult) "" else "Invalid email or password",
                    isLoginSuccessful = loginResult
                )
            }
        }
    }

    fun onRegisterClick() {
        val currentState = _uiState.value

        val isNameInvalid = currentState.name.isBlank()

        val isEmailInvalid = currentState.email.isBlank() ||
                !Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()

        val isPasswordInvalid = currentState.password.isBlank() ||
                currentState.password.length < 6

        val isConfirmPasswordInvalid =
            currentState.confirmPassword.isBlank() ||
                    currentState.confirmPassword != currentState.password

        if (
            isNameInvalid ||
            isEmailInvalid ||
            isPasswordInvalid ||
            isConfirmPasswordInvalid
        ) {
            _uiState.update {
                it.copy(
                    nameError = isNameInvalid,
                    emailError = isEmailInvalid,
                    passwordError = isPasswordInvalid,
                    confirmPasswordError = isConfirmPasswordInvalid,
                    authErrorMessage = "",
                    isRegisterSuccessful = false
                )
            }
            return
        }

        viewModelScope.launch {
            val registerResult = authRepository.registerUser(
                name = currentState.name,
                email = currentState.email,
                password = currentState.password
            )

            _uiState.update {
                it.copy(
                    nameError = false,
                    emailError = false,
                    passwordError = false,
                    confirmPasswordError = false,
                    authErrorMessage = if (registerResult) "" else "Registration failed. Try another email.",
                    isRegisterSuccessful = registerResult
                )
            }
        }
    }

    fun clearLoginSuccess() {
        _uiState.update {
            it.copy(isLoginSuccessful = false)
        }
    }

    fun clearRegisterSuccess() {
        _uiState.update {
            it.copy(isRegisterSuccessful = false)
        }
    }
}