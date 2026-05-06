package com.example.northstar.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.northstar.ui.theme.NeutralCharcoal
import com.example.northstar.ui.theme.NeutralLightGrey
import com.example.northstar.ui.theme.PrimaryBlue

data class Action(
    val icon: ImageVector,
    val label: String,
    val route: String?
)

@Composable
fun QuickActionsRow(navController: NavController) {
    val actions = listOf(
        Action(Icons.Default.Add,       "Add Money", Screen.AddIncome.route),
        Action(Icons.Default.Star,      "Goal",      Screen.Goals.route),
        Action(Icons.Default.Menu,      "Budget",    Screen.AddExpense.route),
        Action(Icons.Default.DateRange, "History",   Screen.Analytics.route),
        Action(Icons.Default.MoreVert,  "More",      null)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        actions.forEach { action ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { action.route?.let { navController.navigate(it) } }
                    .padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(NeutralLightGrey),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        action.icon,
                        contentDescription = action.label,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    action.label,
                    color = NeutralCharcoal.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
        }
    }
}