package com.example.northstar.ui.budget.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeleteOutline
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
import com.example.northstar.ui.theme.InterFontFamily

@Composable
fun BudgetCard(
    budget: Budget,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usageRatio = if (budget.limitAmount > 0) budget.spentAmount.toFloat() / budget.limitAmount.toFloat() else 0f
    val remainingAmount = budget.limitAmount - budget.spentAmount

    val boundaryColor = when {
        usageRatio >= 1.0f -> Color(0xFFD32F2F)
        usageRatio >= 0.8f -> Color(0xFFE65100)
        usageRatio >= 0.5f -> Color(0xFFFBC02D)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(boundaryColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = boundaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = budget.category,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = InterFontFamily
                        )
                        Text(
                            text = "Period: ${budget.period}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = InterFontFamily
                        )
                    }
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Budget",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { usageRatio.coerceAtMost(1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = boundaryColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Spent: LKR ${String.format("%,d", budget.spentAmount)} / LKR ${String.format("%,d", budget.limitAmount)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = InterFontFamily
                )
                Text(
                    text = if (remainingAmount >= 0) "Left: LKR ${String.format("%,d", remainingAmount)}" else "Over: LKR ${String.format("%,d", -remainingAmount)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (remainingAmount >= 0) boundaryColor else Color(0xFFD32F2F),
                    fontFamily = InterFontFamily
                )
            }

            if (usageRatio >= (budget.warningThreshold.toFloat() / 100f)) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (usageRatio >= 1f) Color(0xFFFFEBEE) else Color(0xFFFFF3E0))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (usageRatio >= 1f) "⚠️ Critical Limit Exceeded: Scale down outlays!" else "⚠️ Warning: Budget has crossed ${budget.warningThreshold}% allocation threshold.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (usageRatio >= 1f) Color(0xFFC62828) else Color(0xFFE65100),
                        fontFamily = InterFontFamily
                    )
                }
            }
        }
    }
}