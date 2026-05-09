package com.example.northstar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.northstar.Screen
import com.example.northstar.ui.dashboard.TransactionItem
import java.util.Locale

@Composable
fun TransactionSection(
    navController: NavController,
    transactions: List<TransactionItem> = emptyList()
) {
    Column(modifier = Modifier.padding(horizontal = 0.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Text(
                "Latest Transactions",
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

        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No transactions this month yet",
                    color = DashboardTextMuted,
                    fontSize = 14.sp
                )
            }
        } else {
            transactions.forEach { tx ->
                val tone = transactionTone(tx)
                val amountText = if (tx.isIncome) "+${formatLkr(tx.amount)}" else "-${formatLkr(tx.amount)}"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp), clip = false),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DashboardSurface),
                    border = BorderStroke(1.dp, DashboardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(tone.tileBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = tone.icon,
                                contentDescription = null,
                                tint = tone.iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                tx.title,
                                color = DashboardTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${tx.category.ifBlank { tone.categoryLabel }} • ${tone.formatDateLabel(tx.date)}",
                                color = DashboardTextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                amountText,
                                color = tone.amountColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.3).sp,
                                textAlign = TextAlign.End
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                tone.formatDateLabel(tx.date),
                                color = DashboardTextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

private data class TransactionTone(
    val tileBackground: Color,
    val iconTint: Color,
    val amountColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val categoryLabel: String
) {
    fun formatDateLabel(timestamp: Long): String =
        if (timestamp <= 0L) "Recently" else java.text.SimpleDateFormat("dd MMM", Locale.US).format(java.util.Date(timestamp))
}

private fun transactionTone(tx: TransactionItem): TransactionTone {
    val key = (tx.title + " " + tx.category).lowercase(Locale.US)
    return when {
        tx.isIncome -> TransactionTone(
            tileBackground = DashboardIncomeTile,
            iconTint = DashboardPrimary,
            amountColor = DashboardSuccess,
            icon = Icons.Default.KeyboardArrowDown,
            categoryLabel = "Income"
        )

        key.contains("rent") -> TransactionTone(
            tileBackground = DashboardRentTile,
            iconTint = DashboardDestructive,
            amountColor = DashboardDestructive,
            icon = Icons.Default.Close,
            categoryLabel = "Rent"
        )

        else -> TransactionTone(
            tileBackground = DashboardOtherTile,
            iconTint = Color(0xFFE67700),
            amountColor = DashboardDestructive,
            icon = Icons.Default.Close,
            categoryLabel = "Other"
        )
    }
}

