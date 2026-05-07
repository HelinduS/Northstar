package com.example.northstar.ui.dashboard

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

data class DashboardUiState(
    val displayName: String = "",
    val email: String = "",
    val totalIncomeLkr: Long = 0L,
    val totalExpensesLkr: Long = 0L,
    val netSavedLkr: Long = 0L,
    val committedExpensesLkr: Long = 0L,
    val discretionaryExpensesLkr: Long = 0L,
    val recentTransactions: List<TransactionItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class TransactionItem(
    val id: String = "",
    val title: String = "",
    val amount: Long = 0L,
    val isIncome: Boolean = false,
    val date: String = "",
    val category: String = ""
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val user = firebaseAuth.currentUser
                if (user == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "User not logged in"
                    )
                    return@launch
                }

                // Get user display name and email
                val userDoc = firestore
                    .collection("users")
                    .document(user.uid)
                    .get()
                    .await()

                val displayName = userDoc.getString("displayName") ?: ""
                val email = userDoc.getString("email") ?: ""

                // Get current month range
                val calendar = java.util.Calendar.getInstance()
                calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                val startOfMonth = calendar.time

                // Fetch incomes for current month
                val incomesSnapshot = firestore
                    .collection("users")
                    .document(user.uid)
                    .collection("incomes")
                    .whereGreaterThanOrEqualTo("date", startOfMonth)
                    .get()
                    .await()

                val totalIncome = incomesSnapshot.documents.sumOf {
                    it.getLong("lkrAmount") ?: 0L
                }

                // Fetch expenses for current month
                val expensesSnapshot = firestore
                    .collection("users")
                    .document(user.uid)
                    .collection("expenses")
                    .whereGreaterThanOrEqualTo("date", startOfMonth)
                    .get()
                    .await()

                val totalExpenses = expensesSnapshot.documents.sumOf {
                    it.getLong("amount") ?: 0L
                }

                val committedExpenses = expensesSnapshot.documents
                    .filter { it.getString("expenseType") == "COMMITTED" }
                    .sumOf { it.getLong("amount") ?: 0L }

                val discretionaryExpenses = expensesSnapshot.documents
                    .filter { it.getString("expenseType") == "DISCRETIONARY" }
                    .sumOf { it.getLong("amount") ?: 0L }

                // Build recent transactions list (last 5)
                val recentIncomes = incomesSnapshot.documents.map {
                    TransactionItem(
                        id = it.id,
                        title = it.getString("sourceType") ?: "Income",
                        amount = it.getLong("lkrAmount") ?: 0L,
                        isIncome = true,
                        category = it.getString("sourceType") ?: ""
                    )
                }

                val recentExpenses = expensesSnapshot.documents.map {
                    TransactionItem(
                        id = it.id,
                        title = it.getString("category") ?: "Expense",
                        amount = it.getLong("amount") ?: 0L,
                        isIncome = false,
                        category = it.getString("category") ?: ""
                    )
                }

                val recentTransactions = (recentIncomes + recentExpenses)
                    .sortedByDescending { it.date }
                    .take(5)

                _uiState.value = DashboardUiState(
                    displayName = displayName,
                    email = email,
                    totalIncomeLkr = totalIncome,
                    totalExpensesLkr = totalExpenses,
                    netSavedLkr = totalIncome - totalExpenses,
                    committedExpensesLkr = committedExpenses,
                    discretionaryExpensesLkr = discretionaryExpenses,
                    recentTransactions = recentTransactions,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load dashboard data"
                )
            }
        }
    }
}