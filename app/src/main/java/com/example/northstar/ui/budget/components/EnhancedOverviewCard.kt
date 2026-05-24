package com.example.northstar.ui.budget.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.domain.model.Budget

@Composable
fun EnhancedOverviewCard(budgets: List<Budget>) {
    val totalLimit = budgets.sumOf { it.limitAmount }
    val totalSpent = budgets.sumOf { it.spentAmount }
    val totalRemaining = totalLimit - totalSpent

    val onTrackCount = budgets.count {
        val ratio = if (it.limitAmount > 0) it.spentAmount.toFloat() / it.limitAmount.toFloat() else 0f
        ratio < (it.warningThreshold.toFloat() / 100f)
    }
    val riskExceededCount = budgets.size - onTrackCount

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface  // white in light mode, dark card in dark mode
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)  // subtle shadow
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "COLLECTIVE LIMIT BUDGET CAP",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant  // muted text
            )
            Text(
                text = "LKR ${String.format("%,d", totalLimit)}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "TOTAL SPENT",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "LKR ${String.format("%,d", totalSpent)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "REMAINING FUND",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "LKR ${String.format("%,d", totalRemaining)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (totalRemaining < 0) Color(0xFFD32F2F) else Color(0xFF388E3C)
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "On Track: $onTrackCount",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF388E3C)
                )
                Text(
                    text = "Risk/Exceeded: $riskExceededCount",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE65100)
                )
            }
        }
    }
}