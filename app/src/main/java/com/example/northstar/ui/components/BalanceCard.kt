package com.example.northstar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.theme.NeutralWhite
import com.example.northstar.ui.theme.PrimaryBlue

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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(
                "Total Income This Month",
                color = NeutralWhite.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "LKR ${String.format("%,.2f", incomeLkr)}",
                color = NeutralWhite,
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
                    color = NeutralWhite.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Text(
                    "LKR ${String.format("%,.2f", expensesLkr)}",
                    color = NeutralWhite,
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
                color = NeutralWhite,
                trackColor = NeutralWhite.copy(alpha = 0.3f)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Saved LKR ${String.format("%,.2f", savedLkr)}",
                color = NeutralWhite.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }
    }
}