package com.example.northstar.ui.analytics

import com.example.northstar.domain.model.AnalyticsSummary
import com.example.northstar.domain.model.CategoryBreakdown

/**
 * Data structure representing historical or grouped financial trend data points.
 */
data class TrendData(
    val label: String,
    val incomeAmount: Long,
    val expenseAmount: Long
)

/**
 * UI State container for the Analytics Screen.
 */
data class AnalyticsUiState(
    val selectedTab: AnalyticsTab = AnalyticsTab.EXPENSE,
    val selectedFilter: TimeFilter = TimeFilter.MONTHLY,
    val allTimeSummary: AnalyticsSummary = AnalyticsSummary(0, 0, 0),
    val breakdownList: List<CategoryBreakdown> = emptyList(),
    val isLoading: Boolean = false,
    val trendData: List<TrendData> = emptyList()
)

/**
 * Tabs available in the Analytics section.
 */
enum class AnalyticsTab {
    INCOME,
    EXPENSE,
    COMPARISON
}

/**
 * Time filter options available for sorting financial data.
 */
enum class TimeFilter {
    WEEKLY,
    MONTHLY,
    YEARLY,
    CUSTOM
}