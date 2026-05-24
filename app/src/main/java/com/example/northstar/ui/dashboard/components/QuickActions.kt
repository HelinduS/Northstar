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
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.TrackChanges
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ── Two large primary action cards ────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PrimaryActionCard(
                icon          = Icons.Outlined.Payments,
                title         = "Add Income",
                subtitle      = "Record income",
                iconBg        = GreenAccent.copy(alpha = 0.2f),
                iconTint      = GreenBright,
                cardBg        = GreenDeep,
                titleColor    = White,
                subtitleColor = White.copy(alpha = 0.5f),
                modifier      = Modifier.weight(1f),
                onClick       = { navController.navigate(Screen.AddIncome.route) }
            )
            PrimaryActionCard(
                icon          = Icons.Outlined.Receipt,
                title         = "Add Expense",
                subtitle      = "Track spending",
                iconBg        = NegativeRed.copy(alpha = 0.10f),
                iconTint      = NegativeRed,
                cardBg        = cs.surface,
                titleColor    = cs.onSurface,
                subtitleColor = cs.onSurfaceVariant,
                modifier      = Modifier.weight(1f),
                cardBorder    = cs.outline,
                onClick       = { navController.navigate(Screen.AddExpense.route) }
            )
        }

        // ── Four secondary icon cards (Goals / Analytics / History / Budget) ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecondaryActionCard(
                icon    = Icons.Outlined.TrackChanges,
                label   = "Goals",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(Screen.Goals.route) }
            )
            SecondaryActionCard(
                icon    = Icons.Outlined.QueryStats,
                label   = "Analytics",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(Screen.Analytics.route) }
            )
            SecondaryActionCard(
                icon    = Icons.Outlined.DateRange,
                label   = "History",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(Screen.TransactionHistory.route) }
            )
            SecondaryActionCard(
                icon    = Icons.Outlined.Savings,
                label   = "Budget",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(Screen.Budgets.route) }
            )
        }
    }
}

@Composable
fun PrimaryActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconBg: Color,
    iconTint: Color,
    cardBg: Color,
    titleColor: Color,
    subtitleColor: Color,
    modifier: Modifier = Modifier,
    cardBorder: Color = Color.Transparent,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg, RoundedCornerShape(18.dp))
            .border(1.dp, cardBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 14.sp,
            color = titleColor,
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
            fontWeight = FontWeight.Medium,
            lineHeight = 11.sp,
            color = cs.onSurfaceVariant,
            fontFamily = InterFontFamily
        )
    }
}
