package com.example.northstar.ui.dashboard

import androidx.activity.ComponentActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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