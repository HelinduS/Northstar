package com.example.northstar.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.northstar.Screen
import com.example.northstar.ui.auth.AuthViewModel
import com.example.northstar.ui.components.BalanceCard
import com.example.northstar.ui.components.DashboardHeader
import com.example.northstar.ui.components.QuickActionsRow
import com.example.northstar.ui.components.TransactionSection
import com.example.northstar.ui.components.WalletSection
import com.example.northstar.ui.theme.NeutralWhite

@Composable
fun DashboardScreen(
    navController: NavController,
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by dashboardViewModel.uiState.collectAsState()
    val authViewModel: AuthViewModel = hiltViewModel()
    val onLogoutClick = {
        authViewModel.signOut()
        navController.navigate(Screen.Login.route) {
            popUpTo(Screen.Dashboard.route) { inclusive = true }
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeutralWhite)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        DashboardHeader(
            displayName = uiState.displayName,
            email = uiState.email,
            onSettingsClick = {
                navController.navigate(Screen.Profile.route)
            },
            onProfileClick = {
                navController.navigate(Screen.Profile.route)
            },
            onLogoutClick = onLogoutClick
        )
        Spacer(Modifier.height(4.dp))
        BalanceCard(
            totalIncome = uiState.totalIncomeLkr,
            totalExpenses = uiState.totalExpensesLkr,
            netSaved = uiState.netSavedLkr
        )
        Spacer(Modifier.height(24.dp))
        QuickActionsRow(navController)
        Spacer(Modifier.height(28.dp))
        WalletSection(
            navController = navController,
            committed = uiState.committedExpensesLkr,
            discretionary = uiState.discretionaryExpensesLkr
        )
        Spacer(Modifier.height(28.dp))
        TransactionSection(
            navController = navController,
            transactions = uiState.recentTransactions
        )
    }
}