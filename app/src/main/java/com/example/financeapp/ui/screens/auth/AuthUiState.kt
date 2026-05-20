package com.example.financeapp.ui.screens.auth

data class AuthUiState(
    val name: String = "",
    val nameError: Boolean = false,

    val email: String = "",
    val emailError: Boolean = false,

    val password: String = "",
    val passwordError: Boolean = false,

    val confirmPassword: String = "",
    val confirmPasswordError: Boolean = false,

    val authErrorMessage: String = "",

    val isLoginSuccessful: Boolean = false,
    val isRegisterSuccessful: Boolean = false
)