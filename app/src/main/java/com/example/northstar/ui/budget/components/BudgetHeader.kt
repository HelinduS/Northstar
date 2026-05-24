package com.example.northstar.ui.budget.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.domain.model.Budget
import com.example.northstar.ui.theme.GreenDeep

@Composable
fun BudgetHeader(
    budgets: List<Budget>,
    onBackClick: () -> Unit
) {
    val totalLimit = budgets.sumOf { it.limitAmount }
    val totalSpent = budgets.sumOf { it.spentAmount }
    val totalRemaining = totalLimit - totalSpent
    val progress = if (totalLimit > 0) totalSpent.toFloat() / totalLimit.toFloat() else 0f

    val onTrackCount = budgets.count {
        val ratio = if (it.limitAmount > 0) it.spentAmount.toFloat() / it.limitAmount.toFloat() else 0f
        ratio < (it.warningThreshold.toFloat() / 100f)
    }
    val riskExceededCount = budgets.size - onTrackCount

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenDeep)
    ) {
        // 🔘 White shaded circle (small)
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-40).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Budget Tracker",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "COLLECTIVE LIMIT BUDGET CAP",
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = "LKR ${String.format("%,d", totalLimit)}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                LinearProgressIndicator(
                    progress = { progress.coerceAtMost(1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF52B788),
                    trackColor = Color.White.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "TOTAL SPENT",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "LKR ${String.format("%,d", totalSpent)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "REMAINING FUND",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "LKR ${String.format("%,d", totalRemaining)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (totalRemaining < 0) Color(0xFFFF6B6B) else Color.White
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusPill(
                        label = "On Track",
                        count = onTrackCount,
                        backgroundColor = Color(0xFF2D6A4F),
                        textColor = Color.White
                    )
                    StatusPill(
                        label = "Risk/Exceeded",
                        count = riskExceededCount,
                        backgroundColor = Color(0xFFFF8C00).copy(alpha = 0.2f),
                        textColor = Color(0xFFFFB347)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusPill(label: String, count: Int, backgroundColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(32.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "$label: $count",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}