package com.example.northstar.ui.budget

import com.example.northstar.domain.model.Budget

// Filter and Sort Configuration
data class BudgetFilterState(
    val status: String = "ALL",          // "ALL", "ON_TRACK", "AT_RISK", "EXCEEDED"
    val period: String = "ALL",          // "ALL", "WEEKLY", "MONTHLY", "QUARTERLY", "YEARLY"
    val categoryGroup: String = "ALL"    // "ALL", "ESSENTIAL", "LIFESTYLE"
)

enum class BudgetSortOption {
    HIGHEST_SPENDING,      // highest percentage spent
    LOWEST_SPENDING,       // lowest percentage spent
    LOWEST_REMAINING,      // smallest remaining LKR value
    MOST_REMAINING         // largest remaining LKR value
}

// Interactive Real-Time Alerts Structure
data class BudgetSystemAlert(
    val id: String,
    val budgetCategory: String,
    val type: AlertType,
    val message: String,
    val isSnoozed: Boolean = false,
    val snoozeUntil: Long = 0L
)

enum class AlertType { THRESHOLD_WARNING, EXCEEDED_CRITICAL, PERIOD_ENDING }

// Comprehensive State Wrapper
data class EnhancedBudgetUiState(
    val budgets: List<Budget> = emptyList(),
    val filteredBudgets: List<Budget> = emptyList(),
    val activeAlerts: List<BudgetSystemAlert> = emptyList(),
    val filter: BudgetFilterState = BudgetFilterState(),
    val sortBy: BudgetSortOption = BudgetSortOption.LOWEST_SPENDING,  // changed from HIGHEST_SPENDING
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)