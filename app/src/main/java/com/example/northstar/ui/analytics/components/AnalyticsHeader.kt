package com.example.northstar.ui.analytics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.theme.*

@Composable
fun AnalyticsHeader(income: Long, expense: Long, netSaved: Long) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HeaderCard("Income", income, Modifier.weight(1f))
        HeaderCard("Expense", expense, Modifier.weight(1f))
        HeaderCard("Saved", netSaved, Modifier.weight(1f))
    }
}

@Composable
private fun HeaderCard(label: String, amount: Long, modifier: Modifier) {

    val subtleGradient = Brush.verticalGradient(
        colors = listOf(
            GreenDeep,
            Color(0xFF2E7D32)
        ),
        startY = 0.0f,
        endY = 0.6f
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(subtleGradient)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                Text(
                    text = "Rs.${String.format("%.2f", amount / 100.0)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}