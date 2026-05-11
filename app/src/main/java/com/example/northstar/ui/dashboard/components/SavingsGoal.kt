package com.example.northstar.ui.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.theme.*
import com.example.northstar.domain.model.Goal
import java.util.*
import java.text.SimpleDateFormat

@Composable
fun SavingsGoalCard(goals: List<Goal> = emptyList()) {
    Column(modifier = Modifier.padding(top = 20.dp)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Savings Goal", fontSize = 15.sp, fontWeight = FontWeight.W700, color = TextPrimary, fontFamily = InterFontFamily)
            Text("${goals.size}", fontSize = 11.sp, fontWeight = FontWeight.W600, color = TextSecondary, fontFamily = InterFontFamily)
        }

        if (goals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(White, RoundedCornerShape(20.dp))
                    .border(1.dp, Border, RoundedCornerShape(20.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "🎯",
                        fontSize = 32.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        "No savings goals yet",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W600,
                        color = TextPrimary,
                        fontFamily = InterFontFamily,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        "Create a goal to get started",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontFamily = InterFontFamily
                    )
                }
            }
        } else {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(goals) { goal ->
                    GoalCardItem(goal = goal)
                }
            }
        }
    }
}

@Composable
private fun GoalCardItem(goal: Goal) {
    val dateFormatter = remember { SimpleDateFormat("MMM yyyy", Locale.US) }
    val targetDate = if (goal.targetDate > 0L) dateFormatter.format(Date(goal.targetDate)) else "No date"
    
    val daysRemaining = if (goal.targetDate > 0L) {
        val diff = goal.targetDate - System.currentTimeMillis()
        (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
    } else 0
    
    val monthsRemaining = (daysRemaining / 30.0).toInt().coerceAtLeast(0)
    
    val monthlyNeeded = if (goal.targetDate > 0L && daysRemaining > 0) {
        val remaining = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0L)
        (remaining / (daysRemaining / 30.0)).toLong()
    } else 0L
    
    Box(
        modifier = Modifier
            .width(280.dp)
            .background(White, RoundedCornerShape(20.dp))
            .border(1.dp, Border, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ── Goal ring ──
                Box(
                    modifier = Modifier.size(70.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(70.dp)) {
                        val stroke = 6.dp.toPx()
                        val inset = stroke / 2f
                        val arcSize = Size(size.width - stroke, size.height - stroke)
                        val topLeft = Offset(inset, inset)

                        // track
                        drawArc(
                            color = Separator,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = stroke)
                        )
                        // progress
                        val progress = if (goal.targetAmount > 0) (goal.savedAmount.toFloat() / goal.targetAmount) else 0f
                        val sweepAngle = progress * 360f
                        drawArc(
                            color = Navy900,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${(if (goal.targetAmount > 0) (goal.savedAmount.toFloat() / goal.targetAmount * 100).toInt() else 0)}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W800,
                            color = TextPrimary,
                            fontFamily = InterFontFamily
                        )
                        Text(
                            "saved",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.W500,
                            color = TextMuted,
                            fontFamily = InterFontFamily
                        )
                    }
                }

                // ── Goal info ──
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        goal.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W700,
                        color = TextPrimary,
                        fontFamily = InterFontFamily,
                        modifier = Modifier.padding(bottom = 6.dp),
                        maxLines = 2
                    )
                    Row(
                        modifier = Modifier.padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            String.format(Locale.US, "LKR %.0f", goal.savedAmount / 100.0),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W800,
                            color = TextPrimary,
                            letterSpacing = (-0.6).sp,
                            fontFamily = InterFontFamily
                        )
                        Text(
                            String.format(Locale.US, "  of LKR %.0f", goal.targetAmount / 100.0),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.W500,
                            color = TextMuted,
                            fontFamily = InterFontFamily
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Separator, RoundedCornerShape(99.dp))
                    ) {
                        val progress = if (goal.targetAmount > 0) goal.savedAmount.toFloat() / goal.targetAmount else 0f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress.coerceIn(0f, 1f))
                                .height(4.dp)
                                .background(Navy900, RoundedCornerShape(99.dp))
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(Separator)
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            // Stat cells
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        String.format(Locale.US, "LKR %.0f", monthlyNeeded / 100.0),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W700,
                        color = TextPrimary,
                        fontFamily = InterFontFamily
                    )
                    Text(
                        "NEEDED/MO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.W600,
                        color = TextMuted,
                        letterSpacing = 0.5.sp,
                        fontFamily = InterFontFamily,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .width(0.5.dp)
                        .height(34.dp)
                        .background(Separator)
                )
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$monthsRemaining mo",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W700,
                        color = TextPrimary,
                        fontFamily = InterFontFamily
                    )
                    Text(
                        "REMAINING",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.W600,
                        color = TextMuted,
                        letterSpacing = 0.5.sp,
                        fontFamily = InterFontFamily,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .width(0.5.dp)
                        .height(34.dp)
                        .background(Separator)
                )
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        targetDate,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W700,
                        color = TextPrimary,
                        fontFamily = InterFontFamily
                    )
                    Text(
                        "PROJECTED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.W600,
                        color = TextMuted,
                        letterSpacing = 0.5.sp,
                        fontFamily = InterFontFamily,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}