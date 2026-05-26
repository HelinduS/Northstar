package com.example.northstar.ui.dashboard

import androidx.activity.ComponentActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.northstar.Screen
import com.example.northstar.ui.dashboard.components.*
import com.example.northstar.ui.notifications.NotificationPanel
import com.example.northstar.ui.notifications.NotificationViewModel
import com.example.northstar.ui.theme.*

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

    // Auto reload when screen comes back into focus
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateAsState()

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            dashboardViewModel.loadDashboardData()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { dashboardViewModel.loadDashboardData() },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = WindowInsets.navigationBars
                    .add(WindowInsets(bottom = 92.dp))
                    .asPaddingValues()
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
                            .background(MaterialTheme.colorScheme.background)
                            .padding(top = 8.dp, bottom = 20.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            QuickActionsRow(navController)
                            SavingsGoalCard(
                                goals = uiState.goals,
                                avgMonthlySavings = uiState.avgMonthlySavings
                            )
                            ThisMonthCard(uiState.totalIncomeLkr, uiState.totalExpensesLkr)
                            TransactionsList(uiState.recentTransactions) {
                                navController.navigate(Screen.TransactionHistory.route)
                            }
                        }
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
                onMarkAllRead = { notificationViewModel.markAllAsRead() },
                onMarkRead = { notificationViewModel.markAsRead(it) },
                onDelete = { notificationViewModel.deleteNotification(it) },
                onDismiss = { showNotifications = false }
            )
        }
    }
}