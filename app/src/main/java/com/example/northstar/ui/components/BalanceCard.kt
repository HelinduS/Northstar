package com.example.northstar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.theme.PrimaryBlue
import java.util.Locale

@Composable
fun BalanceCard(
    totalIncome: Long = 0L,
    totalExpenses: Long = 0L,
    netSaved: Long = 0L
) {
    // Convert from paisa to LKR for display
    val incomeLkr = totalIncome / 100.0
    val expensesLkr = totalExpenses / 100.0
    val savedLkr = netSaved / 100.0

    // Progress = expenses / income (capped at 1.0)
    val spentProgress = if (totalIncome > 0)
        (totalExpenses.toFloat() / totalIncome.toFloat()).coerceIn(0f, 1f)
    else 0f

    val cardShape = RoundedCornerShape(24.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(
                elevation = 10.dp,
                shape = cardShape,
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor = Color.Black.copy(alpha = 0.18f)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.65f),
                shape = cardShape
            )
            .clip(cardShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFF4F7FB),
                        Color(0xFFE8EEF8)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(
                "Total Income This Month",
                color = PrimaryBlue.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "LKR ${String.format(Locale.US, "%,.2f", incomeLkr)}",
                color = PrimaryBlue,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(18.dp))
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween
            ) {
                Text(
                    "Total Expenses",
                    color = PrimaryBlue.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Text(
                    "LKR ${String.format(Locale.US, "%,.2f", expensesLkr)}",
                    color = PrimaryBlue,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { spentProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = PrimaryBlue,
                trackColor = PrimaryBlue.copy(alpha = 0.3f)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Saved LKR ${String.format(Locale.US, "%,.2f", savedLkr)}",
                color = PrimaryBlue.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }
    }
}