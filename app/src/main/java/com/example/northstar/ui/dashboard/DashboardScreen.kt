package com.example.northstar.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.northstar.ui.components.DashboardPageBackground
import androidx.compose.foundation.layout.statusBarsPadding

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


    LaunchedEffect(Unit) {
        dashboardViewModel.loadDashboardData()
    }


    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DashboardPageBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardPageBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 90.dp)
    ) {
        DashboardSectionReveal(delayMillis = 0) {
            DashboardHeader(
                displayName = uiState.displayName,
                email = uiState.email,
                onNotificationClick = {},
                onProfileClick = {},
                onLogoutClick = onLogoutClick
            )
        }

        Spacer(Modifier.height(18.dp))

        DashboardSectionReveal(delayMillis = 40) {
            BalanceCard(
                totalIncome = uiState.totalIncomeLkr,
                totalExpenses = uiState.totalExpensesLkr,
                netSaved = uiState.netSavedLkr
            )
        }

        Spacer(Modifier.height(18.dp))

        DashboardSectionReveal(delayMillis = 80) {
            QuickActionsRow(navController)
        }

        Spacer(Modifier.height(18.dp))

        DashboardSectionReveal(delayMillis = 120) {
            WalletSection(
                navController = navController,
                committed = uiState.committedExpensesLkr,
                discretionary = uiState.discretionaryExpensesLkr
            )
        }

        Spacer(Modifier.height(18.dp))

        DashboardSectionReveal(delayMillis = 160) {
            TransactionSection(
                navController = navController,
                transactions = uiState.recentTransactions
            )
        }
    }
}

@Composable
private fun DashboardSectionReveal(
    delayMillis: Int,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(delayMillis) {
        kotlinx.coroutines.delay(delayMillis.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 10 })
    ) {
        content()
    }
}
