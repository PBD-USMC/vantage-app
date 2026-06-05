package com.example.financeapp.ui.screens.auth

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(
        context = application.applicationContext
    )

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onNameChange(newValue: String) {
        _uiState.update {
            it.copy(
                name = newValue,
                nameError = false,
                authErrorMessage = "",
                authSuccessMessage = "",
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
                authSuccessMessage = "",
                isLoginSuccessful = false,
                isRegisterSuccessful = false,
                isPasswordResetEmailSent = false
            )
        }
    }

    fun onPasswordChange(newValue: String) {
        _uiState.update {
            it.copy(
                password = newValue,
                passwordError = false,
                authErrorMessage = "",
                authSuccessMessage = "",
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
                authSuccessMessage = "",
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
                    authSuccessMessage = "",
                    isLoginSuccessful = false
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    authErrorMessage = "",
                    authSuccessMessage = ""
                )
            }

            val loginResult = authRepository.loginUser(
                email = currentState.email.trim(),
                password = currentState.password
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    emailError = false,
                    passwordError = false,
                    authErrorMessage = if (loginResult.isSuccessful) "" else loginResult.message,
                    authSuccessMessage = "",
                    isLoginSuccessful = loginResult.isSuccessful
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
                    authSuccessMessage = "",
                    isRegisterSuccessful = false
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    authErrorMessage = "",
                    authSuccessMessage = ""
                )
            }

            val registerResult = authRepository.registerUser(
                name = currentState.name.trim(),
                email = currentState.email.trim(),
                password = currentState.password
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    nameError = false,
                    emailError = false,
                    passwordError = false,
                    confirmPasswordError = false,
                    authErrorMessage = if (registerResult.isSuccessful) "" else registerResult.message,
                    authSuccessMessage = "",
                    isRegisterSuccessful = registerResult.isSuccessful
                )
            }
        }
    }

    fun onForgotPasswordClick() {
        val currentState = _uiState.value

        val isEmailInvalid = currentState.email.isBlank() ||
                !Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()

        if (isEmailInvalid) {
            _uiState.update {
                it.copy(
                    emailError = true,
                    authErrorMessage = "",
                    authSuccessMessage = "",
                    isPasswordResetEmailSent = false
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    emailError = false,
                    authErrorMessage = "",
                    authSuccessMessage = "",
                    isPasswordResetEmailSent = false
                )
            }

            val resetResult = authRepository.sendPasswordResetEmail(
                email = currentState.email.trim()
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    authErrorMessage = if (resetResult.isSuccessful) "" else resetResult.message,
                    authSuccessMessage = if (resetResult.isSuccessful) resetResult.message else "",
                    isPasswordResetEmailSent = resetResult.isSuccessful
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

    fun clearPasswordResetStatus() {
        _uiState.update {
            it.copy(
                isPasswordResetEmailSent = false,
                authSuccessMessage = "",
                authErrorMessage = ""
            )
        }
    }
}