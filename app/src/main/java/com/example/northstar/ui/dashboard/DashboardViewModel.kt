package com.example.northstar.ui.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.northstar.data.repository.GoalRepository
import com.example.northstar.domain.model.Goal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

data class DashboardUiState(
    val displayName: String = "",
    val email: String = "",
    val totalIncomeLkr: Long = 0L,
    val totalExpensesLkr: Long = 0L,
    val netSavedLkr: Long = 0L,
    val committedExpensesLkr: Long = 0L,
    val discretionaryExpensesLkr: Long = 0L,
    val allTimeIncomeLkr: Long = 0L,
    val allTimeExpensesLkr: Long = 0L,
    val allTimeNetSavedLkr: Long = 0L,
    val recentTransactions: List<TransactionItem> = emptyList(),
    val goals: List<Goal> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class TransactionItem(
    val id: String = "",
    val title: String = "",
    val amount: Long = 0L,
    val isIncome: Boolean = false,
    val date: Long = 0L,
    val category: String = "",
    val expenseType: String = "",
    val paymentMethod: String = "",
    val description: String = "",
    val sourceType: String = "",
    val originalCurrency: String = "",
    val originalAmount: Long = 0L,
    val exchangeRate: Double = 1.0,
    val notes: String = ""
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    // Time-based greeting — recomputed every time it's accessed
    val greeting: Pair<String, ImageVector>
        get() {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            return when (hour) {
                in 5..11  -> Pair("Good morning,",  Icons.Filled.WbSunny)
                in 12..16 -> Pair("Good afternoon,", Icons.Filled.WbSunny)
                in 17..20 -> Pair("Good evening,",  Icons.Filled.WbTwilight)
                else      -> Pair("Good night,",    Icons.Filled.Bedtime)
            }
        }

    init {
        loadDashboardData()
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            goalRepository.getAllGoals().collect { goals ->
                _uiState.value = _uiState.value.copy(goals = goals)
            }
        }
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

                val userDoc = firestore
                    .collection("users")
                    .document(user.uid)
                    .get()
                    .await()

                val displayName = userDoc.getString("displayName") ?: ""
                val email = userDoc.getString("email") ?: ""

                val calendar = java.util.Calendar.getInstance()
                calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                val startOfMonth = calendar.time

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

                val allTimeIncomesSnapshot = firestore
                    .collection("users")
                    .document(user.uid)
                    .collection("incomes")
                    .get()
                    .await()

                val allTimeIncome = allTimeIncomesSnapshot.documents.sumOf {
                    it.getLong("lkrAmount") ?: 0L
                }

                val allTimeExpensesSnapshot = firestore
                    .collection("users")
                    .document(user.uid)
                    .collection("expenses")
                    .get()
                    .await()

                val allTimeExpenses = allTimeExpensesSnapshot.documents.sumOf {
                    it.getLong("amount") ?: 0L
                }

                val recentIncomes = incomesSnapshot.documents.map {
                    TransactionItem(
                        id = it.id,
                        title = it.getString("sourceType") ?: "Income",
                        amount = it.getLong("amountLKR") ?: 0L,
                        isIncome = true,
                        date = it.getTimestamp("date")?.toDate()?.time ?: 0L,
                        category = it.getString("sourceType") ?: "",
                        sourceType = it.getString("sourceType") ?: "",
                        originalCurrency = it.getString("originalCurrency") ?: "LKR",
                        originalAmount = it.getLong("originalAmount") ?: 0L,
                        exchangeRate = it.getDouble("exchangeRate") ?: 1.0,
                        notes = it.getString("notes") ?: ""
                    )
                }

                val recentExpenses = expensesSnapshot.documents.map {
                    TransactionItem(
                        id = it.id,
                        title = it.getString("category") ?: "Expense",
                        amount = it.getLong("amount") ?: 0L,
                        isIncome = false,
                        date = it.getTimestamp("date")?.toDate()?.time ?: 0L,
                        category = it.getString("category") ?: "",
                        expenseType = it.getString("expenseType") ?: "",
                        paymentMethod = it.getString("paymentMethod") ?: "",
                        description = it.getString("description") ?: ""
                    )
                }

                val recentTransactions = (recentIncomes + recentExpenses)
                    .sortedByDescending { it.date }
                    .take(20)

                _uiState.value = _uiState.value.copy(
                    displayName = displayName,
                    email = email,
                    totalIncomeLkr = totalIncome,
                    totalExpensesLkr = totalExpenses,
                    netSavedLkr = totalIncome - totalExpenses,
                    committedExpensesLkr = committedExpenses,
                    discretionaryExpensesLkr = discretionaryExpenses,
                    allTimeIncomeLkr = allTimeIncome,
                    allTimeExpensesLkr = allTimeExpenses,
                    allTimeNetSavedLkr = allTimeIncome - allTimeExpenses,
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

    fun deleteTransaction(transactionId: String, isIncome: Boolean) {
        viewModelScope.launch {
            try {
                val user = firebaseAuth.currentUser ?: return@launch
                val collection = if (isIncome) "incomes" else "expenses"
                firestore
                    .collection("users")
                    .document(user.uid)
                    .collection(collection)
                    .document(transactionId)
                    .delete()
                    .await()
                loadDashboardData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to delete transaction"
                )
            }
        }
    }
}