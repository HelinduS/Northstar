package com.example.northstar.ui.dashboard

import androidx.activity.ComponentActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.northstar.Screen
import com.example.northstar.ui.dashboard.components.*
import com.example.northstar.ui.notifications.NotificationPanel
import com.example.northstar.ui.notifications.NotificationViewModel
import com.example.northstar.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by dashboardViewModel.uiState.collectAsState()
    val (greetingText, greetingIcon) = remember { dashboardViewModel.greeting }

    val context = LocalContext.current
    val notificationViewModel: NotificationViewModel = viewModel(
        viewModelStoreOwner = context as ComponentActivity
    )
    val notifications by notificationViewModel.notifications.collectAsState()
    val unreadCount by remember { derivedStateOf { notifications.count { !it.isRead } } }

    var showNotifications by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { dashboardViewModel.loadDashboardData() },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
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
                        greetingIcon = greetingIcon,
                        unreadNotificationCount = unreadCount,
                        onNotificationClick = { showNotifications = true }
                    )
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                            .background(White)
                            .padding(top = 8.dp, bottom = 20.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            QuickActionsRow(navController)
                            SavingsGoalCard(goals = uiState.goals)
                            ThisMonthCard(uiState.totalIncomeLkr, uiState.totalExpensesLkr)
                            TransactionsList(uiState.recentTransactions) {
                                navController.navigate(Screen.TransactionHistory.route)
                            }
                        }
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

        // Dim background overlay
        AnimatedVisibility(
            visible = showNotifications,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { showNotifications = false }
            )
        }

        // Notification panel sliding up
        AnimatedVisibility(
            visible = showNotifications,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(
                animationSpec = tween(300),
                initialOffsetY = { it }
            ),
            exit = slideOutVertically(
                animationSpec = tween(300),
                targetOffsetY = { it }
            )
        ) {
            NotificationPanel(
                notifications = notifications,
                onMarkAllRead = { notificationViewModel.markAllRead() },        // was markAllAsRead
                onMarkRead = { notificationViewModel.markRead(it) },            // was markAsRead
                onDelete = { notificationViewModel.delete(it) },    // was delete
                onDismiss = { showNotifications = false }
            )
        }
    }
}

