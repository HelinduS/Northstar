package com.example.northstar.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.northstar.ui.components.BalanceCard
import com.example.northstar.ui.components.DashboardHeader
import com.example.northstar.ui.components.QuickActionsRow
import com.example.northstar.ui.components.TransactionSection
import com.example.northstar.ui.components.WalletSection
import com.example.northstar.ui.theme.NeutralWhite

@Composable
fun DashboardScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeutralWhite)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        DashboardHeader()
        Spacer(Modifier.height(4.dp))
        BalanceCard()
        Spacer(Modifier.height(24.dp))
        QuickActionsRow(navController)
        Spacer(Modifier.height(28.dp))
        WalletSection(navController)
        Spacer(Modifier.height(28.dp))
        TransactionSection(navController)
    }
}