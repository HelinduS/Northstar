package com.example.northstar.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.northstar.domain.model.Budget
import com.example.northstar.ui.budget.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onBackClick: () -> Unit,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var activeModalBudget by remember { mutableStateOf<Budget?>(null) }
    var targetDeleteCategory by remember { mutableStateOf<String?>(null) }
    var detailFocusedBudget by remember { mutableStateOf<Budget?>(null) }
    var showCreateModal by remember { mutableStateOf(false) }

    if (detailFocusedBudget != null) {
        BudgetDetailsScreen(
            budget = detailFocusedBudget!!,
            onBackClick = { detailFocusedBudget = null }
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            BudgetHeader(budgets = uiState.budgets, onBackClick = onBackClick)
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Button(
                    onClick = { showCreateModal = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create Budget", modifier = Modifier.size(18.dp))
                        Text("Create Budget", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                SystemAlertsListSection(
                    alerts = uiState.activeAlerts,
                    onViewAlertDetails = { cat ->
                        detailFocusedBudget = uiState.budgets.find { it.category == cat }
                    },
                    onSnoozeAlert = { viewModel.snoozeAlert(it) },
                    onDismissAlert = { viewModel.snoozeAlert(it) }
                )

                FilterAndSortControlBar(
                    filter = uiState.filter,
                    sortBy = uiState.sortBy,
                    matchCount = uiState.filteredBudgets.size,
                    onFilterChange = { viewModel.updateFilters(it) },
                    onSortChange = { viewModel.updateSorting(it) }
                )
            }
        }

        items(uiState.filteredBudgets, key = { it.category }) { budget ->
            AdaptiveBudgetCard(
                budget = budget,
                onView = { detailFocusedBudget = budget },
                onEdit = { activeModalBudget = budget },
                onDelete = { targetDeleteCategory = budget.category },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        if (uiState.filteredBudgets.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No budgets yet. Create your first budget to start tracking spending limits.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    if (showCreateModal) {
        BudgetFormModal(
            onDismiss = { showCreateModal = false },
            onConfirm = { cat, lim, prd, thrs, start, end ->
                viewModel.addOrUpdateBudget(cat, lim, prd, thrs, start, end)
                showCreateModal = false
            }
        )
    }
    if (activeModalBudget != null) {
        BudgetFormModal(
            existingBudget = activeModalBudget,
            onDismiss = { activeModalBudget = null },
            onConfirm = { cat, lim, prd, thrs, start, end ->
                viewModel.addOrUpdateBudget(cat, lim, prd, thrs, start, end)
                activeModalBudget = null
            }
        )
    }
    if (targetDeleteCategory != null) {
        BudgetDeleteConfirmation(
            categoryName = targetDeleteCategory!!,
            onDismiss = { targetDeleteCategory = null },
            onConfirmDelete = {
                viewModel.removeBudget(targetDeleteCategory!!)
                targetDeleteCategory = null
            }
        )
    }
}