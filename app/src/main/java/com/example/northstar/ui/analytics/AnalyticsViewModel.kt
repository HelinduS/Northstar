package com.example.northstar.ui.analytics

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.northstar.data.repository.ExpenseRepository
import com.example.northstar.data.repository.IncomeRepository
import com.example.northstar.domain.model.AnalyticsSummary
import com.example.northstar.domain.model.CategoryBreakdown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    // Reactive state drivers
    private val _selectedTab = MutableStateFlow(AnalyticsTab.EXPENSE)
    private val _selectedFilter = MutableStateFlow(TimeFilter.MONTHLY)
    private val _customDateRange = MutableStateFlow<Pair<Long, Long>?>(null)

    // Tracks all-time database health totals
    private val allTimeSummaryFlow = combine(
        incomeRepository.getIncomesByDateRange(0, 4102425000000L),
        expenseRepository.getExpensesByDateRange(0, 4102425000000L)
    ) { inc, exp ->
        val totalInc = inc.sumOf { it.amountLKR }
        val totalExp = exp.sumOf { it.amount }
        AnalyticsSummary(totalInc, totalExp, totalInc - totalExp)
    }

    // Main UI State flow: Cleanly pipe parameters via flatMapLatest to eliminate blocking .first() invocations
    val uiState: StateFlow<AnalyticsUiState> = combine(
        _selectedTab, _selectedFilter, _customDateRange
    ) { tab, filter, custom ->
        val range = if (filter == TimeFilter.CUSTOM && custom != null) custom else calculateRange(filter)
        Triple(tab, filter, range)
    }.flatMapLatest { (tab, filter, range) ->
        combine(
            incomeRepository.getIncomesByDateRange(range.first, range.second),
            expenseRepository.getExpensesByDateRange(range.first, range.second),
            allTimeSummaryFlow
        ) { incs, exps, summary ->
            val breakdown = when (tab) {
                AnalyticsTab.INCOME -> calculateIncomeBreakdown(incs)
                AnalyticsTab.EXPENSE -> calculateExpenseBreakdown(exps)
                AnalyticsTab.COMPARISON -> calculateComparisonBreakdown(incs, exps)
            }

            AnalyticsUiState(
                selectedTab = tab,
                selectedFilter = filter,
                allTimeSummary = summary,
                breakdownList = breakdown,
                isLoading = false,
                trendData = if (tab == AnalyticsTab.COMPARISON) generateTrendData(incs, exps, filter) else emptyList()
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState(isLoading = true)
    )

    // UI Action handlers
    fun selectTab(tab: AnalyticsTab) { _selectedTab.value = tab }
    fun selectFilter(filter: TimeFilter) { _selectedFilter.value = filter }

    fun onCustomRangeSelected(start: Long, end: Long) {
        val cal = Calendar.getInstance().apply {
            timeInMillis = end
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }
        _customDateRange.value = Pair(start, cal.timeInMillis)
        _selectedFilter.value = TimeFilter.CUSTOM
    }

    // Maps Expense categories
    private fun getExpenseColor(cat: String): Color = when (cat.uppercase()) {
        "RENT" -> Color(0xFF3498DB)
        "FOOD" -> Color(0xFFE67E22)
        "TRANSPORT" -> Color(0xFF2ECC71)
        "SUBSCRIPTIONS" -> Color(0xFF9B59B6)
        "UTILITIES" -> Color(0xFF1ABC9C)
        "ENTERTAINMENT" -> Color(0xFFF1C40F)
        "HEALTH" -> Color(0xFFE74C3C)
        "SHOPPING" -> Color(0xFFEC407A)
        "CRYPTO" -> Color(0xFF34495E)
        "OTHER" -> Color(0xFF95A5A6)
        else -> generateVibrantColor(cat)
    }

    // Maps Income sources
    private fun getIncomeColor(src: String): Color = when (src.uppercase()) {
        "SALARY" -> Color(0xFF27AE60)
        "FREELANCE" -> Color(0xFF2980B9)
        "SOCIAL MEDIA" -> Color(0xFFE1306C)
        "GOOGLE ADSENSE" -> Color(0xFFF39C12)
        "INVESTMENTS" -> Color(0xFFB5E18B)
        "E-COMMERCE" -> Color(0xFFD35400)
        "AFFILIATE" -> Color(0xFFC0392B)
        "CRYPTO" -> Color(0xFF2C3E50)
        "DIGITAL PRODUCTS" -> Color(0xFF8E44AD)
        "TUTORING" -> Color(0xFF5DF8D8)
        "OTHER" -> Color(0xFF7F8C8D)
        else -> generateVibrantColor(src)
    }

    private fun generateVibrantColor(seed: String): Color {
        val colors = listOf(Color(0xFF1ABC9C), Color(0xFF3498DB), Color(0xFFE67E22), Color(0xFFE74C3C))
        return colors[Math.abs(seed.hashCode()) % colors.size]
    }

    private fun calculateExpenseBreakdown(exps: List<com.example.northstar.domain.model.Expense>) = exps.groupBy { it.category }.map { (cat, list) ->
        val sum = list.sumOf { it.amount }
        CategoryBreakdown(cat, sum, sum.toFloat() / exps.sumOf { it.amount }.coerceAtLeast(1), getExpenseColor(cat))
    }.sortedByDescending { it.totalAmount }

    private fun calculateIncomeBreakdown(incs: List<com.example.northstar.domain.model.Income>) = incs.groupBy { it.sourceType }.map { (src, list) ->
        val sum = list.sumOf { it.amountLKR }
        CategoryBreakdown(src, sum, sum.toFloat() / incs.sumOf { it.amountLKR }.coerceAtLeast(1), getIncomeColor(src))
    }.sortedByDescending { it.totalAmount }

    private fun calculateComparisonBreakdown(incs: List<com.example.northstar.domain.model.Income>, exps: List<com.example.northstar.domain.model.Expense>): List<CategoryBreakdown> {
        val totalInc = incs.sumOf { it.amountLKR }
        val totalExp = exps.sumOf { it.amount }
        val combined = totalInc + totalExp
        if (combined == 0L) return emptyList()
        return listOf(
            CategoryBreakdown("Income", totalInc, totalInc.toFloat() / combined, Color(0xFF2ECC71)),
            CategoryBreakdown("Expenses", totalExp, totalExp.toFloat() / combined, Color(0xFFE74C3C))
        )
    }

    private fun generateTrendData(
        incs: List<com.example.northstar.domain.model.Income>,
        exps: List<com.example.northstar.domain.model.Expense>,
        filter: TimeFilter
    ): List<TrendData> {
        val cal = Calendar.getInstance()
        return when (filter) {
            TimeFilter.WEEKLY -> {
                val labels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                labels.mapIndexed { i, label ->
                    val inc = incs.filter { cal.timeInMillis = it.receivedDate; cal.get(Calendar.DAY_OF_WEEK) == i + 1 }.sumOf { it.amountLKR }
                    val exp = exps.filter { cal.timeInMillis = it.date; cal.get(Calendar.DAY_OF_WEEK) == i + 1 }.sumOf { it.amount }
                    TrendData(label, inc, exp)
                }
            }
            TimeFilter.YEARLY -> {
                val labels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                labels.mapIndexed { i, label ->
                    val inc = incs.filter { cal.timeInMillis = it.receivedDate; cal.get(Calendar.MONTH) == i }.sumOf { it.amountLKR }
                    val exp = exps.filter { cal.timeInMillis = it.date; cal.get(Calendar.MONTH) == i }.sumOf { it.amount }
                    TrendData(label, inc, exp)
                }
            }
            else -> { // MONTHLY or CUSTOM range partitions data into progressive chronological weeks
                val labels = listOf("W1", "W2", "W3", "W4+")
                labels.mapIndexed { i, label ->
                    val inc = incs.filter {
                        cal.timeInMillis = it.receivedDate
                        val weekIndex = ((cal.get(Calendar.DAY_OF_MONTH) - 1) / 7).coerceAtMost(3)
                        weekIndex == i
                    }.sumOf { it.amountLKR }
                    val exp = exps.filter {
                        cal.timeInMillis = it.date
                        val weekIndex = ((cal.get(Calendar.DAY_OF_MONTH) - 1) / 7).coerceAtMost(3)
                        weekIndex == i
                    }.sumOf { it.amount }
                    TrendData(label, inc, exp)
                }
            }
        }
    }

    private fun calculateRange(filter: TimeFilter): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        val end = cal.timeInMillis
        when (filter) {
            TimeFilter.WEEKLY -> cal.add(Calendar.DAY_OF_YEAR, -7)
            TimeFilter.MONTHLY -> cal.set(Calendar.DAY_OF_MONTH, 1)
            TimeFilter.YEARLY -> cal.set(Calendar.DAY_OF_YEAR, 1)
            else -> {}
        }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return Pair(cal.timeInMillis, end)
    }
}