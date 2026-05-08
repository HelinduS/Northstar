package com.example.northstar.ui.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.northstar.data.repository.IncomeRepository
import com.example.northstar.domain.model.Income
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// 1. PLACE UI STATE HERE
sealed class IncomeUiState {
    object Idle : IncomeUiState()
    object Loading : IncomeUiState()
    object Success : IncomeUiState()
    data class Error(val message: String) : IncomeUiState()
}

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val repository: IncomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<IncomeUiState>(IncomeUiState.Idle)
    val uiState: StateFlow<IncomeUiState> = _uiState.asStateFlow()

    fun addIncome(
        sourceType: String,
        projectName: String?,
        amountStr: String,
        currency: String,
        exchangeRate: Double,
        date: Long,
        notes: String?
    ) {
        val amount = amountStr.toLongOrNull() ?: 0L

        if (amount <= 0) {
            _uiState.value = IncomeUiState.Error("Amount must be greater than zero.")
            return
        }

        viewModelScope.launch {
            _uiState.value = IncomeUiState.Loading

            val income = Income(
                id = UUID.randomUUID().toString(),
                sourceType = sourceType,
                projectName = projectName,
                originalAmount = amount,
                originalCurrency = currency,
                lkrAmount = (amount * exchangeRate).toLong(),
                exchangeRate = exchangeRate,
                date = date,
                notes = notes,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            val result = repository.addIncome(income)

            _uiState.value = if (result.isSuccess) {
                IncomeUiState.Success
            } else {
                IncomeUiState.Error(result.exceptionOrNull()?.message ?: "Failed to save.")
            }
        }
    }

    fun resetState() {
        _uiState.value = IncomeUiState.Idle
    }
}