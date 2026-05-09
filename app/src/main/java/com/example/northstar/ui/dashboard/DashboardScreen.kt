package com.example.northstar.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.northstar.Screen
import com.example.northstar.ui.dashboard.components.*
import com.example.northstar.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    navController: NavController,
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by dashboardViewModel.uiState.collectAsState()

    Scaffold(containerColor = Surface) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(bottom = 100.dp)
        ) {
            item { HeroSection(uiState.displayName, uiState.netSavedLkr, uiState.totalIncomeLkr, uiState.totalExpensesLkr) }
            item { QuickActionsRow(navController) }
            item { SavingsGoalCard() }
            item { ThisMonthCard(uiState.totalIncomeLkr, uiState.totalExpensesLkr) }
            item { TransactionsList(uiState.recentTransactions) { navController.navigate(Screen.TransactionHistory.route) } }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun QuickActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(White, RoundedCornerShape(16.dp))
                .border(1.dp, Border, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(19.dp),
                tint = Navy900
            )
        }
        Text(
            label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            fontFamily = InterFontFamily
        )
    }
}

@Composable
fun TransactionRow(transaction: TransactionItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color(0xFFFEF2F2), RoundedCornerShape(12.dp))
                .border(1.dp, Border, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Home,
                contentDescription = transaction.category,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFFDC2626)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                transaction.category.ifBlank { "Transaction" },
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                letterSpacing = (-0.1).sp,
                fontFamily = InterFontFamily
            )
            Text(
                SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date(transaction.date)),
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = TextMuted,
                fontFamily = InterFontFamily,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            val amountLkr = transaction.amount / 100.0
            Text(
                String.format(Locale.US, "LKR %.2f", amountLkr),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (transaction.isIncome) Credit else Debit,
                letterSpacing = (-0.3).sp,
                fontFamily = InterFontFamily
            )
            Text(
                SimpleDateFormat("hh:mm a", Locale.US).format(Date(transaction.date)),
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = TextHint,
                fontFamily = InterFontFamily,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}