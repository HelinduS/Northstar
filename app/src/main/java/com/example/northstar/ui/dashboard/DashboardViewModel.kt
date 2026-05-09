package com.example.northstar.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration // Added for real-time listener
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
    val date: Long = 0L,
    val category: String = ""
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    // --- CHANGE START: Added listener variable ---
    private var incomeListener: ListenerRegistration? = null
    // --- CHANGE END ---

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val user = firebaseAuth.currentUser
                if (user == null) {
                    _uiState.value = DashboardUiState(
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


                incomeListener = firestore
                    .collection("users")
                    .document(user.uid)
                    .collection("incomes")
                    .whereGreaterThanOrEqualTo("date", startOfMonth)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) return@addSnapshotListener

                        val totalIncome = snapshot?.documents?.sumOf {
                            it.getLong("lkrAmount") ?: 0L
                        } ?: 0L

                        val recentIncomes = snapshot?.documents?.map {
                            TransactionItem(
                                id = it.id,
                                title = it.getString("sourceType") ?: "Income",
                                amount = it.getLong("lkrAmount") ?: 0L,
                                isIncome = true,
                                date = it.getTimestamp("date")?.toDate()?.time ?: 0L,
                                category = it.getString("sourceType") ?: ""
                            )
                        } ?: emptyList()

                        // Update state with new income data and recalculate totals
                        _uiState.value = _uiState.value.copy(
                            displayName = displayName,
                            email = email,
                            totalIncomeLkr = totalIncome,
                            netSavedLkr = totalIncome - _uiState.value.totalExpensesLkr,
                            recentTransactions = (recentIncomes + _uiState.value.recentTransactions.filter { !it.isIncome })
                                .sortedByDescending { it.date }
                                .take(5),
                            isLoading = false
                        )
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

                val recentExpenses = expensesSnapshot.documents.map {
                    TransactionItem(
                        id = it.id,
                        title = it.getString("category") ?: "Expense",
                        amount = it.getLong("amount") ?: 0L,
                        isIncome = false,
                        date = it.getTimestamp("date")?.toDate()?.time ?: 0L,
                        category = it.getString("category") ?: ""
                    )
                }

                _uiState.value = _uiState.value.copy(
                    totalExpensesLkr = totalExpenses,
                    netSavedLkr = _uiState.value.totalIncomeLkr - totalExpenses,
                    committedExpensesLkr = committedExpenses,
                    discretionaryExpensesLkr = discretionaryExpenses,
                    recentTransactions = (_uiState.value.recentTransactions.filter { it.isIncome } + recentExpenses)
                        .sortedByDescending { it.date }
                        .take(5)
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load dashboard data"
                )
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        incomeListener?.remove()
    }

}