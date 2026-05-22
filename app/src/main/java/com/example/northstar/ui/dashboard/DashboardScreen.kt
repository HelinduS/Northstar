package com.example.northstar.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.northstar.Screen
import com.example.northstar.ui.dashboard.components.*
import com.example.northstar.ui.theme.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by dashboardViewModel.uiState.collectAsState()
    val (greetingText, greetingIcon) = remember { dashboardViewModel.greeting }

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { dashboardViewModel.loadDashboardData() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 20.dp)
        ) {
            item {
                HeroSection(
                    displayName = uiState.displayName,
                    totalBalance = uiState.netSavedLkr,
                    income = uiState.totalIncomeLkr,
                    expenses = uiState.totalExpensesLkr,
                    allTimeBalance = uiState.allTimeNetSavedLkr,
                    allTimeIncome = uiState.allTimeIncomeLkr,
                    allTimeExpenses = uiState.allTimeExpensesLkr,
                    greetingText = greetingText,
                    greetingIcon = greetingIcon
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(bottom = 20.dp)
                ) {
                    QuickActionsRow(navController)
                    SavingsGoalCard(goals = uiState.goals)
                    TodaysSpendCard(todaysExpense = uiState.totalExpensesLkr)
                    ThisMonthCard(uiState.totalIncomeLkr, uiState.totalExpensesLkr)
                    TransactionsList(uiState.recentTransactions) {
                        navController.navigate(Screen.TransactionHistory.route)
                    }
                }
            }
        }
    }
}

