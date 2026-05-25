package com.example.northstar.ui.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.northstar.domain.model.CategoryBreakdown
import com.example.northstar.ui.analytics.components.*
import com.example.northstar.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(navController: NavController, viewModel: AnalyticsViewModel = hiltViewModel()) {
    val cs = MaterialTheme.colorScheme
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showDatePicker by remember { mutableStateOf(false) }
    var tempStart by remember { mutableStateOf<Long?>(null) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false; tempStart = null },
            confirmButton = {
                TextButton(onClick = {
                    val selected = datePickerState.selectedDateMillis
                    if (tempStart == null) {
                        tempStart = selected
                    } else if (selected != null && selected > tempStart!!) {
                        viewModel.onCustomRangeSelected(tempStart!!, selected)
                        showDatePicker = false
                        tempStart = null
                    }
                }) { Text(if (tempStart == null) "Next" else "Apply", color = cs.primary, fontWeight = FontWeight.Bold) }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = { Text(if (tempStart == null) "Select Start Date" else "Select End Date", modifier = Modifier.padding(16.dp)) },
                showModeToggle = false,
                colors = DatePickerDefaults.colors(selectedDayContainerColor = GreenDeep)
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenDeep,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = cs.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))

                AnalyticsHeader(
                    uiState.allTimeSummary.totalIncome,
                    uiState.allTimeSummary.totalExpenses,
                    uiState.allTimeSummary.netSaved
                )
                Spacer(modifier = Modifier.height(24.dp))

                AnalyticsControls(
                    selectedTab = uiState.selectedTab,
                    selectedFilter = uiState.selectedFilter,
                    onTabChanged = { viewModel.selectTab(it) },
                    onFilterChanged = {
                        if (it == TimeFilter.CUSTOM) showDatePicker = true else viewModel.selectFilter(it)
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Expense grouping chips (only when Expense tab is selected)
            if (uiState.selectedTab == AnalyticsTab.EXPENSE) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = uiState.selectedExpenseGrouping == ExpenseGrouping.BY_CATEGORY,
                            onClick = { viewModel.selectExpenseGrouping(ExpenseGrouping.BY_CATEGORY) },
                            label = { Text("Expense Categories") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GreenDeep,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = uiState.selectedExpenseGrouping == ExpenseGrouping.BY_TYPE,
                            onClick = { viewModel.selectExpenseGrouping(ExpenseGrouping.BY_TYPE) },
                            label = { Text("Expense Type") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GreenDeep,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (uiState.isLoading) {
                item { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = cs.primary) } }
            } else if (uiState.breakdownList.isEmpty()) {
                item { Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { Text("No data found") } }
            } else {
                item {
                    if (uiState.selectedTab == AnalyticsTab.COMPARISON) {
                        if (uiState.selectedFilter == TimeFilter.CUSTOM) {
                            val totalIncome = uiState.breakdownList.find { it.categoryName == "Income" }?.totalAmount ?: 0L
                            val totalExpense = uiState.breakdownList.find { it.categoryName == "Expenses" }?.totalAmount ?: 0L
                            AnalyticsCharts(
                                data = uiState.breakdownList,
                                tab = uiState.selectedTab,
                                totalIncome = totalIncome,
                                totalExpense = totalExpense
                            )
                        } else {
                            ComparisonBarChart(trendData = uiState.trendData)
                        }
                    } else {
                        val total = uiState.breakdownList.sumOf { it.totalAmount }
                        AnalyticsCharts(
                            data = uiState.breakdownList,
                            tab = uiState.selectedTab,
                            totalIncome = if (uiState.selectedTab == AnalyticsTab.INCOME) total else uiState.breakdownList.find { it.categoryName == "Income" }?.totalAmount ?: 0L,
                            totalExpense = if (uiState.selectedTab == AnalyticsTab.EXPENSE) total else uiState.breakdownList.find { it.categoryName == "Expenses" }?.totalAmount ?: 0L
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("Breakdown", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = cs.onSurface)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                items(uiState.breakdownList) { item ->
                    AnalyticsBreakdown(item)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                // Increased bottom spacer to prevent content being hidden by bottom navigation bar
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun AnalyticsBreakdown(item: CategoryBreakdown) {
    val cs = MaterialTheme.colorScheme
    val percentageLabel = String.format("%.1f", item.percentage * 100)

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(10.dp), shape = CircleShape, color = item.color) {}
                Spacer(modifier = Modifier.width(12.dp))
                Text(item.categoryName, fontSize = 14.sp, color = cs.onSurface, fontWeight = FontWeight.Medium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Rs. ${String.format("%.2f", item.totalAmount / 100.0)}",
                    fontWeight = FontWeight.Bold,
                    color = cs.onSurface,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "($percentageLabel%)", fontSize = 12.sp, color = cs.onSurfaceVariant)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { item.percentage },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = item.color,
            trackColor = item.color.copy(alpha = 0.1f),
            strokeCap = StrokeCap.Round
        )
    }
}