package com.example.northstar.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material.icons.outlined.Savings // Added for Budget feature representation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.northstar.Screen
import com.example.northstar.ui.theme.*

@Composable
fun QuickActionsRow(navController: NavController) {
    val cs = MaterialTheme.colorScheme
    val scrollState = rememberScrollState() // Remembers scroll configuration safely

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Features",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = cs.onSurface,
            fontFamily = InterFontFamily,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        // Transformed from fixed layout into a smooth horizontal scrolling arrangement
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SecondaryActionCard(
                icon = Icons.Outlined.TrackChanges,
                label = "Goals",
                modifier = Modifier.width(100.dp),
                onClick = { navController.navigate(Screen.Goals.route) }
            )

            SecondaryActionCard(
                icon = Icons.Outlined.QueryStats,
                label = "Analytics",
                modifier = Modifier.width(100.dp),
                onClick = { navController.navigate(Screen.Analytics.route) }
            )

            SecondaryActionCard(
                icon = Icons.Outlined.DateRange,
                label = "History",
                modifier = Modifier.width(100.dp),
                onClick = { navController.navigate(Screen.TransactionHistory.route) }
            )

            // Milestone 1 Addition: Budget Action Shortcut Option Card
            SecondaryActionCard(
                icon = Icons.Outlined.Savings,
                label = "Budget",
                modifier = Modifier.width(100.dp),
                onClick = { navController.navigate(Screen.Budgets.route) }
            )
        }
    }
}

@Composable
fun PrimaryActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    subtitleColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(20.dp),
                tint = contentColor
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                fontFamily = InterFontFamily
            )
            Text(
                subtitle,
                fontSize = 11.sp,
                lineHeight = 11.sp,
                color = subtitleColor,
                fontFamily = InterFontFamily
            )
        }
    }
}

@Composable
private fun SecondaryActionCard(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cs.surface, RoundedCornerShape(16.dp))
            .border(1.dp, cs.outline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(cs.surfaceVariant, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(18.dp),
                tint = GreenDeep
            )
        }
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = cs.onSurface,
            fontFamily = InterFontFamily
        )
    }
}