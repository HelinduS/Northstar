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
    val phone: String = "",
    val address: String = "",
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

        // Set email immediately from Firebase Auth
        _uiState.value = _uiState.value.copy(
            email = user?.email ?: ""
        )

        // Load ALL profile fields (name, phone, address) from Firestore
        // because Firebase Auth displayName is not set during registration
        user?.uid?.let { uid ->
            viewModelScope.launch {
                val result = authRepository.getUserProfile(uid)
                result.onSuccess { data ->
                    _uiState.value = _uiState.value.copy(
                        displayName = data["displayName"] as? String ?: "",
                        phone = data["phone"] as? String ?: "",
                        address = data["address"] as? String ?: ""
                    )
                }
            }
        }
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

    fun updatePhone(newPhone: String) {
        if (newPhone.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Phone number cannot be empty")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "", successMessage = "")
            val uid = authRepository.currentUser?.uid ?: run {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "User not found")
                return@launch
            }
            val result = authRepository.updateUserField(uid, "phone", newPhone)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    phone = newPhone,
                    successMessage = "Phone updated successfully"
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to update phone"
                )
            }
        }
    }

    fun updateAddress(newAddress: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "", successMessage = "")
            val uid = authRepository.currentUser?.uid ?: run {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "User not found")
                return@launch
            }
            val result = authRepository.updateUserField(uid, "address", newAddress)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    address = newAddress,
                    successMessage = "Address updated successfully"
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to update address"
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