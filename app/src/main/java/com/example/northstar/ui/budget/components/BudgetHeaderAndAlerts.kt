package com.example.northstar.ui.budget.components


import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.budget.BudgetFilterState
import com.example.northstar.ui.budget.BudgetSortOption
import com.example.northstar.ui.budget.BudgetSystemAlert
import com.example.northstar.ui.budget.AlertType

@Composable
fun BudgetScreenHeader(
    onCreateBudgetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Budget Matrix Workspace",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Track limit safety thresholds across outlays",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Button(
            onClick = onCreateBudgetClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Create Budget", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterAndSortControlBar(
    filter: BudgetFilterState,
    sortBy: BudgetSortOption,
    matchCount: Int,
    onFilterChange: (BudgetFilterState) -> Unit,
    onSortChange: (BudgetSortOption) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("ALL", "ON_TRACK", "AT_RISK", "EXCEEDED").forEach { status ->
                FilterChip(
                    selected = filter.status == status,
                    onClick = { onFilterChange(filter.copy(status = status)) },
                    label = { Text(status.replace("_", " ")) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$matchCount matching budgets found", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            var sortMenuExpanded by remember { mutableStateOf(false) }
            Box {
                InputChip(
                    selected = true,
                    onClick = { sortMenuExpanded = true },
                    label = { Text("Sort By") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                )
                DropdownMenu(expanded = sortMenuExpanded, onDismissRequest = { sortMenuExpanded = false }) {

                    DropdownMenuItem(
                        text = { Text("Lowest Spending (%)") },
                        onClick = { onSortChange(BudgetSortOption.LOWEST_SPENDING); sortMenuExpanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Highest Spending (%)") },
                        onClick = { onSortChange(BudgetSortOption.HIGHEST_SPENDING); sortMenuExpanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Highest Remaining (LKR)") },
                        onClick = { onSortChange(BudgetSortOption.MOST_REMAINING); sortMenuExpanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Lowest Remaining (LKR)") },
                        onClick = { onSortChange(BudgetSortOption.LOWEST_REMAINING); sortMenuExpanded = false }
                    )
                }
            }
        }
    }
}

@Composable
fun SystemAlertsListSection(
    alerts: List<BudgetSystemAlert>,
    onViewAlertDetails: (String) -> Unit,
    onSnoozeAlert: (String) -> Unit,
    onDismissAlert: (String) -> Unit
) {
    if (alerts.none { it.isSnoozed }) return

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        alerts.filter { !it.isSnoozed }.forEach { alert ->
            val tintColor = when (alert.type) {
                AlertType.EXCEEDED_CRITICAL -> Color(0xFFD32F2F)
                AlertType.THRESHOLD_WARNING -> Color(0xFFE65100)
                AlertType.PERIOD_ENDING -> Color(0xFF1976D2)
            }
            val bgColor = when (alert.type) {
                AlertType.EXCEEDED_CRITICAL -> Color(0xFFFFEBEE)
                AlertType.THRESHOLD_WARNING -> Color(0xFFFFF3E0)
                AlertType.PERIOD_ENDING -> Color(0xFFE3F2FD)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(
                        imageVector = if (alert.type == AlertType.EXCEEDED_CRITICAL) Icons.Default.Dangerous else Icons.Default.Warning,
                        contentDescription = null,
                        tint = tintColor
                    )
                    Column {
                        Text(alert.message, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            TextButton(onClick = { onViewAlertDetails(alert.budgetCategory) }, contentPadding = PaddingValues(0.dp)) {
                                Text("View Details", fontSize = 11.sp, color = tintColor, fontWeight = FontWeight.Bold)
                            }
                            TextButton(onClick = { onSnoozeAlert(alert.id) }, contentPadding = PaddingValues(0.dp)) {
                                Text("Snooze 24h", fontSize = 11.sp, color = Color.DarkGray)
                            }
                            TextButton(onClick = { onDismissAlert(alert.id) }, contentPadding = PaddingValues(0.dp)) {
                                Text("Dismiss", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}