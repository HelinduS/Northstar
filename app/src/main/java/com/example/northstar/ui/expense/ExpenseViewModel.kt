package com.example.northstar.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class ExpenseUiState(
    val expenses: List<ExpenseItem> = emptyList(),
    val totalExpensesLkr: Long = 0L,
    val committedExpensesLkr: Long = 0L,
    val discretionaryExpensesLkr: Long = 0L,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
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
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    init {
        loadExpenses()
    }

    fun loadExpenses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val user = firebaseAuth.currentUser ?: run {
                    _uiState.value = ExpenseUiState(
                        isLoading = false,
                        error = "User not logged in"
                    )
                    return@launch
                }

                val snapshot = firestore
                    .collection("users")
                    .document(user.uid)
                    .collection("expenses")
                    .get()
                    .await()

                val expenses = snapshot.documents.map {
                    ExpenseItem(
                        id = it.id,
                        amount = it.getLong("amount") ?: 0L,
                        category = it.getString("category") ?: "",
                        expenseType = it.getString("expenseType") ?: "",
                        paymentMethod = it.getString("paymentMethod") ?: "",
                        description = it.getString("description") ?: "",
                        date = it.getTimestamp("date")?.toDate()?.time ?: 0L
                    )
                }

                val total = expenses.sumOf { it.amount }
                val committed = expenses
                    .filter { it.expenseType == "COMMITTED" }
                    .sumOf { it.amount }
                val discretionary = expenses
                    .filter { it.expenseType == "DISCRETIONARY" }
                    .sumOf { it.amount }

                _uiState.value = ExpenseUiState(
                    expenses = expenses,
                    totalExpensesLkr = total,
                    committedExpensesLkr = committed,
                    discretionaryExpensesLkr = discretionary,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load expenses"
                )
            }
        }
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
            try {
                val user = firebaseAuth.currentUser ?: run {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "User not logged in",
                        isSaved = false
                    )
                    return@launch
                }

                val expense = hashMapOf(
                    "amount" to amount,
                    "category" to category,
                    "expenseType" to expenseType,
                    "paymentMethod" to paymentMethod,
                    "description" to description,
                    "date" to com.google.firebase.Timestamp(java.util.Date(date)),
                    "createdAt" to com.google.firebase.Timestamp.now(),
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )

                firestore
                    .collection("users")
                    .document(user.uid)
                    .collection("expenses")
                    .add(expense)
                    .await()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSaved = true
                )

                loadExpenses()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to add expense"
                )
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            try {
                val user = firebaseAuth.currentUser ?: run {
                    _uiState.value = _uiState.value.copy(
                        error = "User not logged in"
                    )
                    return@launch
                }

                firestore
                    .collection("users")
                    .document(user.uid)
                    .collection("expenses")
                    .document(expenseId)
                    .delete()
                    .await()

                loadExpenses()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to delete expense"
                )
            }
        }
    }

    fun resetSavedState() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }
}