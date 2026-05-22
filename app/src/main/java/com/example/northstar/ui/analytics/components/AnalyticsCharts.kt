package com.example.northstar.ui.analytics.components

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
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

    val maxValRaw = trendData.maxOfOrNull { maxOf(it.incomeAmount, it.expenseAmount) }?.coerceAtLeast(1L) ?: 1L
    val maxVal = (maxValRaw * 1.1).toLong()

    // State to track which bar is being touched/hovered
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.fillMaxWidth().height(280.dp).padding(16.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(trendData) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val position = event.changes.first().position

                            // Detect if the finger/cursor is released or moved away
                            val isOutside = position.x < 0 || position.x > size.width || position.y < 0 || position.y > size.height
                            val isReleased = event.type == PointerEventType.Release || event.type == PointerEventType.Exit

                            if (isOutside || isReleased) {
                                selectedIndex = null
                            } else {
                                val labelWidth = 90f
                                val chartWidth = size.width - labelWidth
                                val spacing = if (trendData.size > 7) 12f else 25f
                                val groupTotalWidth = (chartWidth - (trendData.size * spacing)) / trendData.size

                                val relativeX = position.x - labelWidth
                                val index = (relativeX / (groupTotalWidth + spacing)).toInt()

                                selectedIndex = if (index in trendData.indices && position.x >= labelWidth) {
                                    index
                                } else {
                                    null
                                }
                            }
                        }
                    }
                }
        ) {
            val canvasHeight = size.height
            val canvasWidth = size.width
            val labelWidth = 90f
            val chartWidth = canvasWidth - labelWidth

            // Draw Y-Axis Ticks
            val tickCount = 4
            val textPaint = Paint().apply {
                color = Color.Gray.toArgb()
                textSize = 24f
                textAlign = Paint.Align.RIGHT
                isFakeBoldText = true
            }

            for (i in 0..tickCount) {
                val y = canvasHeight - (i * (canvasHeight / tickCount))
                val amount = (maxVal / tickCount) * i
                val label = if (amount >= 100000) "${amount / 100000}k" else "${amount / 100}"

                drawLine(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    start = Offset(labelWidth, y),
                    end = Offset(canvasWidth, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
                drawContext.canvas.nativeCanvas.drawText(label, labelWidth - 10f, y + 8f, textPaint)
            }

            // Draw Bars
            val spacing = if (trendData.size > 7) 12f else 25f
            val groupTotalWidth = (chartWidth - (trendData.size * spacing)) / trendData.size
            val barWidth = groupTotalWidth / 2.3f

            trendData.forEachIndexed { i, data ->
                val xBase = labelWidth + (i * (groupTotalWidth + spacing))
                val incH = (data.incomeAmount.toFloat() / maxVal) * canvasHeight
                val expH = (data.expenseAmount.toFloat() / maxVal) * canvasHeight

                val alpha = if (selectedIndex == null || selectedIndex == i) 1f else 0.3f

                drawRect(
                    color = Color(0xFF2ECC71).copy(alpha = alpha),
                    topLeft = Offset(xBase, canvasHeight - incH),
                    size = Size(barWidth, incH)
                )
                drawRect(
                    color = Color(0xFFE74C3C).copy(alpha = alpha),
                    topLeft = Offset(xBase + barWidth + 2, canvasHeight - expH),
                    size = Size(barWidth, expH)
                )

                // Draw Tooltip
                if (selectedIndex == i) {
                    val incText = "Inc: Rs.${String.format("%.2f", data.incomeAmount / 100.0)}"
                    val expText = "Exp: Rs.${String.format("%.2f", data.expenseAmount / 100.0)}"

                    val tooltipPaint = Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 26f
                        isAntiAlias = true
                        isFakeBoldText = true
                    }

                    val bounds = Rect()
                    tooltipPaint.getTextBounds(incText, 0, incText.length, bounds)
                    val bgWidth = (bounds.width() + 50f).coerceAtLeast(180f)
                    val bgHeight = 100f

                    var tooltipX = xBase + groupTotalWidth / 2 - bgWidth / 2
                    tooltipX = tooltipX.coerceIn(labelWidth, canvasWidth - bgWidth)
                    val tooltipY = (canvasHeight - maxOf(incH, expH) - bgHeight - 20f).coerceAtLeast(10f)

                    drawRoundRect(
                        color = Color.Black.copy(alpha = 0.9f),
                        topLeft = Offset(tooltipX, tooltipY),
                        size = Size(bgWidth, bgHeight),
                        cornerRadius = CornerRadius(12f, 12f)
                    )

                    drawContext.canvas.nativeCanvas.drawText(incText, tooltipX + 25f, tooltipY + 40f, tooltipPaint)
                    drawContext.canvas.nativeCanvas.drawText(expText, tooltipX + 25f, tooltipY + 80f, tooltipPaint)
                }
            }
        }

        // X-Axis Labels
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            trendData.forEach {
                Text(
                    text = it.label,
                    fontSize = if(trendData.size > 7) 8.sp else 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(if(trendData.size > 7) 22.dp else 30.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AnalyticsCharts(data: List<CategoryBreakdown>, tab: AnalyticsTab, totalIncome: Long, totalExpense: Long) {
    val cs = MaterialTheme.colorScheme
    val label = when(tab) {
        AnalyticsTab.INCOME -> "Total Income"
        AnalyticsTab.EXPENSE -> "Total Expense"
        else -> "Net Saved"
    }
    val amount = if (tab == AnalyticsTab.COMPARISON) totalIncome - totalExpense
    else (if (tab == AnalyticsTab.INCOME) totalIncome else totalExpense)

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
            Text(label, fontSize = 12.sp, color = cs.onSurfaceVariant)
            Text(
                text = "Rs. ${String.format("%.2f", amount / 100.0)}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = if(tab == AnalyticsTab.COMPARISON && amount < 0) Color(0xFFE74C3C) else cs.onSurface
            )
        }
    }
}