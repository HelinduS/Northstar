package com.example.northstar.ui.analytics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.domain.model.CategoryBreakdown
import com.example.northstar.ui.analytics.AnalyticsTab
import com.example.northstar.ui.analytics.TrendData
import com.example.northstar.ui.theme.*

@Composable
fun ComparisonBarChart(trendData: List<TrendData>) {
    if (trendData.isEmpty()) return
    val maxVal = trendData.maxOfOrNull { maxOf(it.incomeAmount, it.expenseAmount) }?.coerceAtLeast(1L) ?: 1L

    Column(modifier = Modifier.fillMaxWidth().height(250.dp).padding(16.dp)) {
        Canvas(modifier = Modifier.fillMaxWidth().weight(1f)) {
            val spacing = if (trendData.size > 7) 12f else 25f
            val groupWidth = (size.width - (trendData.size * spacing)) / trendData.size
            val barWidth = groupWidth / 2.3f

            trendData.forEachIndexed { i, data ->
                val xBase = i * (groupWidth + spacing)
                val incH = (data.incomeAmount.toFloat() / maxVal) * size.height
                val expH = (data.expenseAmount.toFloat() / maxVal) * size.height
                drawRect(Color(0xFF2ECC71), Offset(xBase, size.height - incH), Size(barWidth, incH))
                drawRect(Color(0xFFE74C3C), Offset(xBase + barWidth + 2, size.height - expH), Size(barWidth, expH))
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            trendData.forEach {
                Text(
                    text = it.label,
                    fontSize = if(trendData.size > 7) 8.sp else 10.sp,
                    color = TextSecondary,
                    modifier = Modifier.width(if(trendData.size > 7) 22.dp else 30.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AnalyticsCharts(data: List<CategoryBreakdown>, tab: AnalyticsTab, totalIncome: Long, totalExpense: Long) {
    val label = when(tab) { AnalyticsTab.INCOME -> "Total Income"; AnalyticsTab.EXPENSE -> "Total Expense"; else -> "Net Saved" }
    val amount = if (tab == AnalyticsTab.COMPARISON) totalIncome - totalExpense else (if (tab == AnalyticsTab.INCOME) totalIncome else totalExpense)

    Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(180.dp)) {
            var start = -90f
            data.forEach {
                val sweep = it.percentage * 360f
                drawArc(it.color, start, sweep, false, style = Stroke(35f, cap = StrokeCap.Round))
                start += sweep
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 12.sp, color = TextSecondary)
            Text("Rs. ${String.format("%.2f", amount / 100.0)}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = if(tab == AnalyticsTab.COMPARISON && amount < 0) Color(0xFFE74C3C) else Navy900)
        }
    }
}