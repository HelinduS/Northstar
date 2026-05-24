package com.example.northstar.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.northstar.data.repository.ExpenseRepository
import com.example.northstar.domain.model.Expense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

data class ExpenseUiState(
    val expenses: List<ExpenseItem> = emptyList(),
    val totalExpensesLkr: Long = 0L,
    val committedExpensesLkr: Long = 0L,
    val discretionaryExpensesLkr: Long = 0L,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val savedAmount: Long = 0L,
    val savedCategory: String = "",
    val budgetPercent: Int = 0,
    val isLargeExpense: Boolean = false
)

data class ExpenseItem(
    val id: String = "",
    val amount: Long = 0L,
    val category: String = "",
    val expenseType: String = "",
    val paymentMethod: String = "",
    val description: String = "",
    val date: Long = 0L
)

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    init {
        observeExpenses()
    }

    private fun observeExpenses() {
        expenseRepository.getAllExpenses()
            .onEach { expenses ->
                val items = expenses.map { e ->
                    ExpenseItem(
                        id           = e.id,
                        amount       = e.amount,
                        category     = e.category,
                        expenseType  = e.expenseType,
                        paymentMethod = e.paymentSource,
                        description  = e.note ?: "",
                        date         = e.date
                    )
                }
                val total         = items.sumOf { it.amount }
                val committed     = items.filter { it.expenseType == "COMMITTED" }.sumOf { it.amount }
                val discretionary = items.filter { it.expenseType == "DISCRETIONARY" }.sumOf { it.amount }
                _uiState.value = _uiState.value.copy(
                    expenses              = items,
                    totalExpensesLkr      = total,
                    committedExpensesLkr  = committed,
                    discretionaryExpensesLkr = discretionary,
                    isLoading             = false
                )
            }
            .launchIn(viewModelScope)
    }

    fun addExpense(
        amount: Long,
        category: String,
        expenseType: String,
        paymentMethod: String,
        description: String,
        date: Long
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Large-expense detection uses the in-memory list (already loaded from Room)
            val avgExpense = _uiState.value.expenses
                .map { it.amount }
                .takeIf { it.isNotEmpty() }
                ?.average() ?: 0.0
            val isLarge = amount > avgExpense * 2 && avgExpense > 0

            val month = SimpleDateFormat("yyyy-MM", Locale.US).format(Date(date))
            val expense = Expense(
                id            = UUID.randomUUID().toString(),
                amount        = amount,
                currency      = "LKR",
                category      = category,
                expenseType   = expenseType,
                paymentSource = paymentMethod,
                note          = description.ifEmpty { null },
                date          = date,
                month         = month,
                createdAt     = System.currentTimeMillis(),
                updatedAt     = System.currentTimeMillis()
            )

            expenseRepository.addExpense(expense)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading     = false,
                        isSaved       = true,
                        savedAmount   = amount,
                        savedCategory = category,
                        isLargeExpense = isLarge
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error     = e.message ?: "Failed to add expense"
                    )
                }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expenseId)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "Failed to delete expense"
                    )
                }
        }
    }

    fun resetSavedState() {
        _uiState.value = _uiState.value.copy(
            isSaved        = false,
            savedAmount    = 0L,
            savedCategory  = "",
            budgetPercent  = 0,
            isLargeExpense = false
        )
    }
}
