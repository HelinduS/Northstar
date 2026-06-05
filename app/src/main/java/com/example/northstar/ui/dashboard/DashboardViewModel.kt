package com.example.northstar.ui.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.northstar.data.repository.ExpenseRepository
import com.example.northstar.data.repository.GoalRepository
import com.example.northstar.data.repository.IncomeRepository
import com.example.northstar.domain.model.Expense
import com.example.northstar.domain.model.Goal
import com.example.northstar.domain.model.Income
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
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
    val allTransactions: List<TransactionItem> = emptyList(),
    val goals: List<Goal> = emptyList(),
    val avgMonthlySavings: Long = 0L,
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

private fun Income.toTransactionItem() = TransactionItem(
    id = id,
    title = sourceType,
    amount = amountLKR,
    isIncome = true,
    date = receivedDate,
    category = sourceType,
    sourceType = sourceType,
    originalCurrency = currency,
    originalAmount = amount,
    exchangeRate = exchangeRate,
    notes = note ?: ""
)

private fun Expense.toTransactionItem() = TransactionItem(
    id = id,
    title = category,
    amount = amount,
    isIncome = false,
    date = date,
    category = category,
    expenseType = expenseType,
    paymentMethod = paymentSource,
    description = note ?: ""
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

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
        observeDashboard()
    }

    private fun observeDashboard() {
        val user = firebaseAuth.currentUser
        if (user == null) {
            _uiState.value = DashboardUiState(isLoading = false, error = "User not logged in")
            return
        }
        val displayName = user.displayName ?: ""
        val email = user.email ?: ""
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            combine(
                incomeRepository.getAllIncomes(),
                expenseRepository.getAllExpenses(),
                goalRepository.getAllGoals()
            ) { incomes, expenses, goals ->
                buildUiState(displayName, email, incomes, expenses, goals)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun loadDashboardData() {
        // Data updates reactively via repository snapshot listeners — no manual reload needed
    }

    private fun buildUiState(
        displayName: String,
        email: String,
        incomes: List<Income>,
        expenses: List<Expense>,
        goals: List<Goal>
    ): DashboardUiState {
        val startOfMonth = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val monthIncomes = incomes.filter { it.receivedDate >= startOfMonth }
        val monthExpenses = expenses.filter { it.date >= startOfMonth }

        val totalIncome = monthIncomes.sumOf { it.amountLKR }
        val totalExpenses = monthExpenses.sumOf { it.amount }
        val committedExpenses = monthExpenses.filter { it.expenseType == "COMMITTED" }.sumOf { it.amount }
        val discretionaryExpenses = monthExpenses.filter { it.expenseType == "DISCRETIONARY" }.sumOf { it.amount }

        val allTimeIncome = incomes.sumOf { it.amountLKR }
        val allTimeExpenses = expenses.sumOf { it.amount }

        val threeMonthsAgoMs = Calendar.getInstance().apply { add(Calendar.MONTH, -3) }.timeInMillis
        val threeMonthIncome = incomes.filter { it.receivedDate >= threeMonthsAgoMs }.sumOf { it.amountLKR }
        val threeMonthExpenses = expenses.filter { it.date >= threeMonthsAgoMs }.sumOf { it.amount }
        val avgMonthlySavings = ((threeMonthIncome - threeMonthExpenses) / 3L).coerceAtLeast(0L)

        val recentTransactions = (monthIncomes.map { it.toTransactionItem() } + monthExpenses.map { it.toTransactionItem() })
            .sortedByDescending { it.date }
            .take(5)

        val allTransactions = (incomes.map { it.toTransactionItem() } + expenses.map { it.toTransactionItem() })
            .sortedByDescending { it.date }

        return DashboardUiState(
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
            allTransactions = allTransactions,
            goals = goals,
            avgMonthlySavings = avgMonthlySavings,
            isLoading = false
        )
    }

    fun deleteTransaction(transactionId: String, isIncome: Boolean) {
        viewModelScope.launch {
            if (isIncome) {
                incomeRepository.deleteIncome(transactionId)
            } else {
                expenseRepository.deleteExpense(transactionId)
            }
        }
    }
}
