package com.example.northstar.ui.budget

import com.example.northstar.domain.model.Budget

data class BudgetFilter(
    val selectedMonth: String = "",
    val period: String = "ALL"
)

data class BudgetUiState(
    val budgets: List<Budget> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val activeFilters: BudgetFilter = BudgetFilter()
)