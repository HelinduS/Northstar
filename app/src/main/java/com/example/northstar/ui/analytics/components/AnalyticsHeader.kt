package com.example.northstar.ui.analytics.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        HeaderCard("Income", income, Credit, Modifier.weight(1f))
        HeaderCard("Expense", expense, Debit, Modifier.weight(1f))
        HeaderCard("Saved", netSaved, Navy900, Modifier.weight(1f))
    }
}

@Composable
private fun HeaderCard(label: String, amount: Long, color: Color, modifier: Modifier) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Text("Rs.${amount / 100}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color, maxLines = 1)
        }
    }
}