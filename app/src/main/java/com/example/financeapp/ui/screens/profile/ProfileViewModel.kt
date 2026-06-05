package com.example.financeapp.ui.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(
        context = application.applicationContext
    )

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun onNameChange(newValue: String) {
        _uiState.update {
            it.copy(
                name = newValue,
                nameError = false,
                errorMessage = "",
                successMessage = ""
            )
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = "",
                    successMessage = ""
                )
            }

            val userProfile = authRepository.getCurrentUserProfile()

            if (userProfile == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load profile details."
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    userId = userProfile.userId,
                    name = userProfile.name,
                    email = userProfile.email,
                    isLoading = false,
                    errorMessage = "",
                    successMessage = ""
                )
            }
        }
    }

    fun onSaveClick() {
        val currentState = _uiState.value

        val isNameInvalid = currentState.name.isBlank()

        if (isNameInvalid) {
            _uiState.update {
                it.copy(
                    nameError = true,
                    errorMessage = "",
                    successMessage = ""
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = "",
                    successMessage = ""
                )
            }

            val updateResult = authRepository.updateUserName(
                name = currentState.name.trim()
            )

            _uiState.update {
                it.copy(
                    isSaving = false,
                    nameError = false,
                    errorMessage = if (updateResult) "" else "Failed to update profile.",
                    successMessage = if (updateResult) "Profile updated successfully." else ""
                )
            }
        }
    }

    fun logoutUser() {
        authRepository.logoutUser()
    }
}