package com.example.northstar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.northstar.Screen

private data class Action(
    val icon: ImageVector,
    val label: String,
    val route: String?
)

@Composable
fun QuickActionsRow(navController: NavController) {
    val actions = listOf(
        Action(Icons.Default.Add, "Add Money", Screen.AddIncome.route),
        Action(Icons.Default.Star, "Goal", Screen.Goals.route),
        Action(Icons.Default.List, "Budget", Screen.AddExpense.route),
        Action(Icons.Default.DateRange, "History", Screen.Analytics.route)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        actions.forEach { action ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(2.dp, RoundedCornerShape(15.dp), clip = false)
                        .clip(RoundedCornerShape(15.dp))
                        .background(DashboardSurface)
                        .clickable { action.route?.let { navController.navigate(it) } },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        action.icon,
                        contentDescription = action.label,
                        tint = DashboardPrimary,
                        modifier = Modifier.size(19.dp)
                    )
                }
                Text(
                    action.label,
                    color = DashboardTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            }
        }
    }
}