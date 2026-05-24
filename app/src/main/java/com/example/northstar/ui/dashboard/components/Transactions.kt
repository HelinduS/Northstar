package com.example.northstar.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.dashboard.TransactionItem
import com.example.northstar.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// ── Date grouping ─────────────────────────────────────────────────────────────
private fun dateLabel(timestamp: Long): String {
    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val yesterdayStart = todayStart - 86_400_000L

    return when {
        timestamp >= todayStart     -> "Today"
        timestamp >= yesterdayStart -> "Yesterday"
        else -> SimpleDateFormat("MMM dd", Locale.US).format(Date(timestamp))
    }
}

@Composable
fun TransactionsList(transactions: List<TransactionItem>, onSeeAll: () -> Unit) {
    val cs = MaterialTheme.colorScheme

    val grouped: Map<String, List<TransactionItem>> =
        transactions.groupBy { dateLabel(it.date) }

    Column(modifier = Modifier.padding(top = 20.dp)) {
        // Section header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Recent Transactions",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = cs.onBackground,
                fontFamily = InterFontFamily
            )
            Text(
                "View All",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = GreenAccent,
                modifier = Modifier
                    .background(GreenAccent.copy(alpha = 0.12f), RoundedCornerShape(99.dp))
                    .border(1.dp, GreenAccent.copy(alpha = 0.25f), RoundedCornerShape(99.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .clickable(onClick = onSeeAll),
                fontFamily = InterFontFamily
            )
        }

        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, cs.outline, RoundedCornerShape(20.dp))
                    .background(cs.surface)
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No transactions yet",
                    fontSize = 13.sp,
                    color = cs.onSurfaceVariant,
                    fontFamily = InterFontFamily
                )
            }
        } else {
            grouped.forEach { (label, group) ->
                // Date group header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        label.uppercase(Locale.US),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = cs.onSurfaceVariant,
                        letterSpacing = 0.5.sp,
                        fontFamily = InterFontFamily
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(cs.outlineVariant)
                    )
                }

                // Group card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, cs.outline, RoundedCornerShape(16.dp))
                        .background(cs.surface)
                ) {
                    Column {
                        group.forEachIndexed { index, t ->
                            TransactionRow(t)
                            if (index < group.size - 1) {
                                HorizontalDivider(
                                    color = cs.outlineVariant,
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(horizontal = 14.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun TransactionRow(t: TransactionItem) {
    val cs = MaterialTheme.colorScheme
    val key = (t.title + " " + t.category).lowercase(Locale.US)
    val icon = transactionIcon(t, key)
    val (iconBg, iconTint) = transactionIconColors(t, key)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(cs.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                t.category.ifBlank { t.title.ifBlank { "Transaction" } },
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = cs.onSurface,
                fontFamily = InterFontFamily
            )
            val subtitle = buildString {
                if (t.category.isNotBlank() && t.sourceType.isNotBlank())
                    append(t.sourceType)
                else if (t.expenseType.isNotBlank())
                    append(t.expenseType.lowercase().replaceFirstChar { it.uppercase() })
                if (t.paymentMethod.isNotBlank()) {
                    if (isNotEmpty()) append(" • ")
                    append(t.paymentMethod)
                }
            }.ifBlank {
                SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(t.date))
            }

            Text(
                subtitle,
                fontSize = 10.sp,
                color = cs.onSurfaceVariant,
                fontFamily = InterFontFamily,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            val prefix = if (t.isIncome) "+" else "-"
            Text(
                "$prefix LKR ${String.format(Locale.US, "%.2f", t.amount / 100.0)}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (t.isIncome) GreenAccent else NegativeRed,
                fontFamily = InterFontFamily
            )
            Text(
                SimpleDateFormat("hh:mm a", Locale.US).format(Date(t.date)),
                fontSize = 10.sp,
                color = cs.onSurfaceVariant,
                fontFamily = InterFontFamily,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

private fun transactionIcon(t: TransactionItem, key: String) =
    if (t.isIncome) Icons.Default.KeyboardArrowDown
    else Icons.Default.KeyboardArrowUp

private fun transactionIconColors(t: TransactionItem, key: String): Pair<Color, Color> =
    if (t.isIncome) Pair(GreenAccent.copy(alpha = 0.15f), GreenAccent)
    else Pair(NegativeRed.copy(alpha = 0.1f), NegativeRed)