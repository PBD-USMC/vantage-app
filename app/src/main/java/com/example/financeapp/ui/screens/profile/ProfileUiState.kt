package com.example.financeapp.ui.screens.profile

data class ProfileUiState(
    val userId: String = "",
    val name: String = "",
    val email: String = "",

    val nameError: Boolean = false,

    val isLoading: Boolean = false,
    val isSaving: Boolean = false,

    val errorMessage: String = "",
    val successMessage: String = ""
)