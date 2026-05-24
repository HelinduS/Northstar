package com.example.northstar.ui.budget.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.domain.model.Budget
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdaptiveBudgetCard(
    budget: Budget,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spendRatio = if (budget.limitAmount > 0) budget.spentAmount.toFloat() / budget.limitAmount.toFloat() else 0f
    val remaining = budget.limitAmount - budget.spentAmount

    val indicatorColor = when {
        spendRatio >= 1.0f -> Color(0xFFD32F2F)
        spendRatio >= (budget.warningThreshold.toFloat() / 100f) -> Color(0xFFE65100)
        spendRatio >= 0.5f -> Color(0xFFFBC02D)
        else -> Color(0xFF388E3C)
    }

    val categoryIcon = when (budget.category.trim().uppercase()) {
        "RENT" -> Icons.Outlined.Home
        "FOOD & DRINK", "FOOD & DINING" -> Icons.Outlined.Restaurant
        "TRANSPORT" -> Icons.Outlined.DirectionsCar
        "SUBSCRIPTION", "SUBSCRIPTIONS" -> Icons.Outlined.CardMembership
        "UTILITIES" -> Icons.Outlined.Lightbulb
        "ENTERTAINMENT" -> Icons.Outlined.ConfirmationNumber
        "HEALTH & FITNESS" -> Icons.Outlined.FitnessCenter
        "SHOPPING" -> Icons.Outlined.ShoppingBag
        else -> Icons.Outlined.AccountBalanceWallet
    }

    val configuration = LocalConfiguration.current
    val dateRangeText = remember(budget.startDate, budget.endDate, configuration) {
        if (budget.startDate != null && budget.endDate != null) {
            val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
            "${dateFormat.format(Date(budget.startDate))} - ${dateFormat.format(Date(budget.endDate))}"
        } else {
            budget.period.lowercase().replaceFirstChar { it.uppercase() } + " Cycle"
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(color = indicatorColor.copy(alpha = 0.12f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = null,
                            tint = indicatorColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = budget.category,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = dateRangeText,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Amount row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "LKR ${String.format("%,d", budget.spentAmount)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "of LKR ${String.format("%,d", budget.limitAmount)}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${(spendRatio * 100).toInt()}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = indicatorColor.copy(alpha = 0.7f)
                )
            }

            // Progress bar
            LinearProgressIndicator(
                progress = { spendRatio.coerceAtMost(1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = indicatorColor,
                trackColor = indicatorColor.copy(alpha = 0.15f)
            )

            // Remaining / exceeded + days left
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (remaining >= 0)
                        "LKR ${String.format("%,d", remaining)} remaining"
                    else
                        "LKR ${String.format("%,d", -remaining)} exceeded",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (remaining >= 0) Color(0xFF43A047) else Color(0xFFD32F2F)
                )
                if (budget.startDate != null && budget.endDate != null) {
                    val daysLeft = ((budget.endDate - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt()
                    if (daysLeft >= 0) {
                        Text(
                            text = "$daysLeft days left",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // ✅ Shaded action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // View button (primary color)
                Button(
                    onClick = onView,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text("View", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                }

                // Edit button (warning/orange shade)
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFF3E0), // light orange
                        contentColor = Color(0xFFE65100)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text("Edit", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                }

                // Delete button (error/red shade)
                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE), // light red
                        contentColor = Color(0xFFD32F2F)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text("Delete", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                }
            }
        }
    }
}