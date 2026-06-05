package com.example.northstar.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.northstar.data.repository.BudgetRepository
import com.example.northstar.data.repository.ExpenseRepository
import com.example.northstar.domain.model.Budget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _filterState = MutableStateFlow(BudgetFilterState())
    private val _sortState = MutableStateFlow(BudgetSortOption.LOWEST_SPENDING)
    private val _snoozedAlerts = MutableStateFlow<Set<String>>(emptySet())
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Normalize expense category to match budget category keys
    private fun normalizeExpenseCategory(rawCategory: String): String {
        val upper = rawCategory.trim().uppercase()
        return when (upper) {
            "FOOD", "FOOD & DRINK" -> "FOOD & DINING"
            "HEALTH", "HEALTH & FITNESS" -> "HEALTH & FITNESS"
            "SUBSCRIPTIONS" -> "SUBSCRIPTION"
            else -> upper
        }
    }

    val uiState: StateFlow<EnhancedBudgetUiState> = combine(
        budgetRepository.getAllBudgets(),
        expenseRepository.getAllExpenses(),
        _filterState,
        _sortState,
        _snoozedAlerts
    ) { rawBudgets, allExpenses, filters, sorting, snoozed ->

        val processedBudgets = rawBudgets.map { budget ->
            val budgetLookupKey = normalizeExpenseCategory(budget.category)

            // Calculate total spent within the budget's date range, converting cents → LKR
            val totalSpentLkr = allExpenses
                .filter { expense ->
                    // 1. Category match
                    normalizeExpenseCategory(expense.category) == budgetLookupKey
                }
                .filter { expense ->
                    // 2. Date range filter (only expenses inside budget.startDate..budget.endDate)
                    val expenseDate = expense.date
                    val start = budget.startDate
                    val end = budget.endDate
                    when {
                        start != null && end != null -> expenseDate in start..end
                        start != null && end == null -> expenseDate >= start
                        start == null && end != null -> expenseDate <= end
                        else -> true // fallback (should not happen for custom periods)
                    }
                }
                .sumOf { it.amount / 100 } // Convert cents to LKR

            budget.copy(spentAmount = totalSpentLkr)
        }

        // Generate alerts (unchanged)
        val generatedAlerts = mutableListOf<BudgetSystemAlert>()
        processedBudgets.forEach { budget ->
            val spendRatio = if (budget.limitAmount > 0) budget.spentAmount.toFloat() / budget.limitAmount.toFloat() else 0f
            val warningBoundary = budget.warningThreshold.toFloat() / 100f

            if (spendRatio >= 1.0f) {
                generatedAlerts.add(
                    BudgetSystemAlert(
                        id = "${budget.category}_exceeded",
                        budgetCategory = budget.category,
                        type = AlertType.EXCEEDED_CRITICAL,
                        message = "Critical Limit Breached: '${budget.category}' spending has exceeded its cap limit!"
                    )
                )
            } else if (spendRatio >= warningBoundary) {
                generatedAlerts.add(
                    BudgetSystemAlert(
                        id = "${budget.category}_warning",
                        budgetCategory = budget.category,
                        type = AlertType.THRESHOLD_WARNING,
                        message = "Warning Threshold: '${budget.category}' usage has crossed your designated safety warning mark."
                    )
                )
            }
        }

        // Filter and sort (unchanged)
        val filteredAndSorted = processedBudgets.filter { budget ->
            val matchesPeriod = filters.period == "ALL" || budget.period.equals(filters.period, ignoreCase = true)
            val matchesStatus = when (filters.status) {
                "EXCEEDED" -> budget.spentAmount >= budget.limitAmount
                "AT_RISK" -> {
                    val ratio = if (budget.limitAmount > 0) budget.spentAmount.toFloat() / budget.limitAmount.toFloat() else 0f
                    ratio >= 0.8f && budget.spentAmount < budget.limitAmount
                }
                "ON_TRACK" -> {
                    val ratio = if (budget.limitAmount > 0) budget.spentAmount.toFloat() / budget.limitAmount.toFloat() else 0f
                    ratio < 0.8f
                }
                else -> true
            }
            matchesPeriod && matchesStatus
        }.sortedWith { a, b ->
            when (sorting) {
                BudgetSortOption.HIGHEST_SPENDING -> {
                    val ratioA = if (a.limitAmount > 0) a.spentAmount.toFloat() / a.limitAmount.toFloat() else 0f
                    val ratioB = if (b.limitAmount > 0) b.spentAmount.toFloat() / b.limitAmount.toFloat() else 0f
                    ratioB.compareTo(ratioA)
                }
                BudgetSortOption.LOWEST_SPENDING -> {
                    val ratioA = if (a.limitAmount > 0) a.spentAmount.toFloat() / a.limitAmount.toFloat() else 0f
                    val ratioB = if (b.limitAmount > 0) b.spentAmount.toFloat() / b.limitAmount.toFloat() else 0f
                    ratioA.compareTo(ratioB)
                }
                BudgetSortOption.LOWEST_REMAINING -> {
                    val remA = a.limitAmount - a.spentAmount
                    val remB = b.limitAmount - b.spentAmount
                    remA.compareTo(remB)
                }
                BudgetSortOption.MOST_REMAINING -> {
                    val remA = a.limitAmount - a.spentAmount
                    val remB = b.limitAmount - b.spentAmount
                    remB.compareTo(remA)
                }
            }
        }

        EnhancedBudgetUiState(
            budgets = processedBudgets,
            filteredBudgets = filteredAndSorted,
            activeAlerts = generatedAlerts.map { it.copy(isSnoozed = snoozed.contains(it.id)) },
            filter = filters,
            sortBy = sorting,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EnhancedBudgetUiState(isLoading = true)
    )

    fun updateFilters(newFilter: BudgetFilterState) { _filterState.value = newFilter }
    fun updateSorting(newSort: BudgetSortOption) { _sortState.value = newSort }
    fun snoozeAlert(id: String) { _snoozedAlerts.value = _snoozedAlerts.value + id }

    fun addOrUpdateBudget(category: String, limit: Long, period: String, threshold: Int, startDate: Long?, endDate: Long?) {
        viewModelScope.launch {
            val record = Budget(
                id = category,
                category = category,
                limitAmount = limit,
                spentAmount = 0L,
                period = period,
                warningThreshold = threshold,
                month = "Current",
                createdAt = System.currentTimeMillis(),
                startDate = startDate,
                endDate = endDate
            )
            budgetRepository.addBudget(record)
                .onFailure { e -> _errorMessage.value = e.message ?: "Failed to save budget" }
        }
    }

    fun removeBudget(category: String) {
        viewModelScope.launch {
            budgetRepository.deleteBudget(category)
                .onFailure { e -> _errorMessage.value = e.message ?: "Failed to delete budget" }
        }
    }

    fun clearError() { _errorMessage.value = null }
}