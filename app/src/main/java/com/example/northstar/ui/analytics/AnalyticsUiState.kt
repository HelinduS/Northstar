package com.example.northstar.ui.analytics


import com.example.northstar.domain.model.AnalyticsSummary
import com.example.northstar.domain.model.CategoryBreakdown

// Data structure representing historical or grouped financial trend data points.
data class TrendData(
    val label: String,
    val incomeAmount: Long,
    val expenseAmount: Long
)

// Enum for Expense grouping type (only used when AnalyticsTab.EXPENSE is selected)
enum class ExpenseGrouping {
    BY_CATEGORY,   // Group by expense category (Food, Rent, etc.)
    BY_TYPE        // Group by expense type (Committed vs Discretionary)
}

// UI State container for the Analytics Screen.
data class AnalyticsUiState(
    val selectedTab: AnalyticsTab = AnalyticsTab.INCOME,   // changed from EXPENSE to INCOME
    val selectedFilter: TimeFilter = TimeFilter.WEEKLY,    // changed from MONTHLY to WEEKLY
    val selectedExpenseGrouping: ExpenseGrouping = ExpenseGrouping.BY_CATEGORY,
    val allTimeSummary: AnalyticsSummary = AnalyticsSummary(0, 0, 0),
    val breakdownList: List<CategoryBreakdown> = emptyList(),
    val isLoading: Boolean = false,
    val trendData: List<TrendData> = emptyList()
)

// Tabs available in the Analytics section.
enum class AnalyticsTab {
    INCOME,
    EXPENSE,
    COMPARISON
}

// Time filter options available for sorting financial data.
enum class TimeFilter {
    WEEKLY,
    MONTHLY,
    YEARLY,
    CUSTOM
}