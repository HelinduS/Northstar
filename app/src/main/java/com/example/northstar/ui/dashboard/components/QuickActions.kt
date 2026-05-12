package com.example.northstar.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.northstar.Screen
import com.example.northstar.ui.theme.*

@Composable
fun QuickActionsRow(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickAction(label = "Add Money", icon = Icons.Outlined.Payments, modifier = Modifier.weight(1f), onClick = { navController.navigate(Screen.AddIncome.route) })
        QuickAction(label = "Add Expense", icon = Icons.Outlined.Receipt, modifier = Modifier.weight(1f), onClick = { navController.navigate(Screen.AddExpense.route) })
        QuickAction(label = "Goal",      icon = Icons.Outlined.TrackChanges, modifier = Modifier.weight(1f), onClick = { navController.navigate(Screen.Goals.route) })
        QuickAction(label = "Analytics", icon = Icons.Outlined.QueryStats, modifier = Modifier.weight(1f), onClick = { navController.navigate(Screen.Analytics.route) })
        QuickAction(label = "History",   icon = Icons.Outlined.DateRange, modifier = Modifier.weight(1f), onClick = { navController.navigate(Screen.TransactionHistory.route) })
    }
}

@Composable
fun QuickAction(label: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .background(White, RoundedCornerShape(16.dp))
                .border(1.dp, Border, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(22.dp),
                tint = Navy900
            )
        }
        Text(label, fontSize = 11.sp, color = TextSecondary, fontFamily = InterFontFamily)
    }
}