package com.example.northstar.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.northstar.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val successMessage: String = "",
    val errorMessage: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val user = authRepository.currentUser
        _uiState.value = _uiState.value.copy(
            displayName = user?.displayName ?: "",
            email = user?.email ?: ""
        )
    }

    fun updateDisplayName(newName: String) {
        if (newName.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Name cannot be empty")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "", successMessage = "")
            val result = authRepository.updateDisplayName(newName)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    displayName = newName,
                    successMessage = "Name updated successfully"
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to update name"
                )
            }
        }
    }

    fun updateEmail(newEmail: String, currentPassword: String) {
        if (newEmail.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Email cannot be empty")
            return
        }
        if (currentPassword.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password required to change email")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "", successMessage = "")
            val result = authRepository.updateEmail(newEmail, currentPassword)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    email = newEmail,
                    successMessage = "Email updated successfully"
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to update email"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(successMessage = "", errorMessage = "")
    }

    fun signOut() {
        authRepository.signOut()
    }
}