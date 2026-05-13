package com.example.northstar.domain.model

import androidx.compose.ui.graphics.Color

data class AnalyticsSummary(
    val totalIncome: Long = 0,
    val totalExpenses: Long = 0,
    val netSaved: Long = 0
)

data class CategoryBreakdown(
    val categoryName: String,
    val totalAmount: Long,
    val percentage: Float,
    val color: Color
)

data class ComparisonPoint(
    val label: String,
    val income: Long,
    val expense: Long
)