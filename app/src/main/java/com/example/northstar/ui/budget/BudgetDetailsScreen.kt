package com.example.northstar.ui.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.northstar.domain.model.Budget
import com.example.northstar.domain.model.Expense
import com.example.northstar.ui.theme.InterFontFamily
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDetailsScreen(
    budget: Budget,
    onBackClick: () -> Unit,
    detailsViewModel: BudgetDetailsViewModel = hiltViewModel()
) {
    val expenses by detailsViewModel.allExpenses.collectAsState(initial = emptyList())

    fun normalizeCategory(raw: String): String {
        val upper = raw.trim().uppercase()
        return when (upper) {
            "FOOD", "FOOD & DRINK" -> "FOOD & DINING"
            "HEALTH", "HEALTH & FITNESS" -> "HEALTH & FITNESS"
            "SUBSCRIPTIONS" -> "SUBSCRIPTION"
            else -> upper
        }
    }

    val linkedExpenses = remember(budget, expenses) {
        expenses.filter { expense ->
            val categoryMatches = normalizeCategory(expense.category) == budget.category.trim().uppercase()
            val dateInRange = if (budget.startDate != null && budget.endDate != null) {
                expense.date in budget.startDate..budget.endDate
            } else {
                true
            }
            categoryMatches && dateInRange
        }
    }

    val dailyAllowed = calculateDailyAllowed(budget)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${budget.category} Analysis",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Card - more compact
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Daily Allowed Spend Baseline",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "LKR ${String.format("%,d", dailyAllowed)} / Day",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    LinearProgressIndicator(
                        progress = { (budget.spentAmount.toFloat() / budget.limitAmount.toFloat()).coerceAtMost(1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                }
            }

            // Section title
            Text(
                text = "Historical Linked Outflows",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Transaction list or empty state
            if (linkedExpenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recorded transactions matching '${budget.category}' found in this period.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = InterFontFamily,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(linkedExpenses, key = { it.id }) { expense ->
                        ExpenseHistoryCard(expense)
                    }
                }
            }
        }
    }
}

private fun calculateDailyAllowed(budget: Budget): Long {
    if (budget.startDate == null || budget.endDate == null) {
        return budget.limitAmount / 30
    }
    val millisPerDay = 24L * 60 * 60 * 1000
    val days = ((budget.endDate - budget.startDate) / millisPerDay).toInt() + 1
    val safeDays = if (days < 1) 30 else days
    return budget.limitAmount / safeDays
}

@Composable
fun ExpenseHistoryCard(expense: Expense) {
    val configuration = LocalConfiguration.current
    val dateFormat = remember(configuration) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = expense.category,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = dateFormat.format(Date(expense.date)),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!expense.note.isNullOrBlank()) {
                    Text(
                        text = expense.note,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (expense.paymentSource.isNotBlank()) {
                    Text(
                        text = "Paid via ${expense.paymentSource}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "LKR ${String.format("%,d", expense.amount / 100)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}