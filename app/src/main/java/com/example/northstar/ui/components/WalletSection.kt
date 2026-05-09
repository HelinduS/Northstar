package com.example.northstar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.northstar.Screen

@Composable
fun WalletSection(
    navController: NavController,
    committed: Long = 0L,
    discretionary: Long = 0L
) {
    val total = (committed + discretionary).coerceAtLeast(1L)
    val committedFraction = committed.toFloat() / total.toFloat()
    val discretionaryFraction = discretionary.toFloat() / total.toFloat()

    Column(modifier = Modifier.padding(horizontal = 0.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Text(
                "Wallet",
                color = DashboardTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "See All",
                color = DashboardPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {
                    navController.navigate(Screen.Analytics.route)
                }
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WalletCard(
                title = "Committed",
                amount = formatLkr(committed),
                iconBg = DashboardCommittedTile,
                iconTint = DashboardPrimary,
                progress = committedFraction,
                progressBrush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                    listOf(DashboardPrimary, DashboardPrimaryGradientEnd)
                ),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(Screen.AddIncome.route) }
            )
            WalletCard(
                title = "Discretionary",
                amount = formatLkr(discretionary),
                iconBg = DashboardDiscretionaryTile,
                iconTint = DashboardSuccess,
                progress = discretionaryFraction,
                progressBrush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                    listOf(DashboardSuccess, DashboardSuccess)
                ),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(Screen.AddExpense.route) }
            )
        }
    }
}

@Composable
fun WalletCard(
    title: String,
    amount: String,
    iconBg: Color,
    iconTint: Color,
    progress: Float,
    progressBrush: androidx.compose.ui.graphics.Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(18.dp), clip = false)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DashboardSurface),
        border = BorderStroke(1.dp, DashboardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (title == "Committed") Icons.Default.Refresh else Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = DashboardTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                title,
                color = DashboardTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                amount,
                color = DashboardTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(DashboardTrack)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(99.dp))
                        .background(progressBrush)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = percentLabel(progress),
                color = DashboardTextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}