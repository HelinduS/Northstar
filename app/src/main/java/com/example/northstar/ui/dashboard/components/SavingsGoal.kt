package com.example.northstar.ui.dashboard.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
            Text(
                "Savings Goals",
                fontSize = 15.sp,
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = InterFontFamily
            )
            Text(
                "${goals.size}",
                fontSize = 11.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = InterFontFamily
            )
        }

        if (goals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
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
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = InterFontFamily,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        "Create a goal to get started",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = InterFontFamily
                    )
                }
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
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
    val cs = MaterialTheme.colorScheme
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

    val targetProgress = if (goal.targetAmount > 0)
        (goal.savedAmount.toFloat() / goal.targetAmount).coerceIn(0f, 1f)
    else 0f

    val isComplete = targetProgress >= 1f

    // Animated ring progress
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(targetProgress) {
        animatedProgress.animateTo(
            targetValue = targetProgress,
            animationSpec = tween(durationMillis = 1200, easing = EaseOutCubic)
        )
    }

    // Animated bar progress
    val animatedBarProgress = remember { Animatable(0f) }
    LaunchedEffect(targetProgress) {
        animatedBarProgress.animateTo(
            targetValue = targetProgress,
            animationSpec = tween(durationMillis = 1000, delayMillis = 200, easing = EaseOutCubic)
        )
    }

    val accentBrush = if (isComplete)
        Brush.verticalGradient(listOf(GreenAccent, IncomeGreen))
    else
        Brush.verticalGradient(listOf(GreenDeep, GreenAccent))

    // ── Card shell ──────────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .width(270.dp)
            .background(cs.surface, RoundedCornerShape(20.dp))
            .border(
                width = if (isComplete) 1.5.dp else 1.dp,
                color = if (isComplete) GreenAccent.copy(alpha = 0.5f) else cs.outline,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
    ) {

        // ── Top section: accent bar + body ───────────────────────────────────────
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // 4dp left accent bar — only as tall as the body content
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(accentBrush)
            )

            // Body: padding mirrors prototype (16 top/right/bottom, 12 left)
            Column(
                modifier = Modifier
                    .padding(top = 16.dp, end = 16.dp, bottom = 16.dp, start = 12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val trackColor = cs.outlineVariant
                        Canvas(modifier = Modifier.size(64.dp)) {
                            val stroke  = 5.dp.toPx()
                            // r=26 in a 64px container → 6dp inset each side (matches prototype SVG)
                            val inset   = 6.dp.toPx()
                            val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
                            val topLeft = Offset(inset, inset)

                            // Track
                            drawArc(
                                color      = trackColor,
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter  = false,
                                topLeft    = topLeft,
                                size       = arcSize,
                                style      = Stroke(width = stroke)
                            )
                            // Animated progress arc
                            drawArc(
                                color      = if (isComplete) IncomeGreen else GreenDeep,
                                startAngle = -90f,
                                sweepAngle = animatedProgress.value * 360f,
                                useCenter  = false,
                                topLeft    = topLeft,
                                size       = arcSize,
                                style      = Stroke(width = stroke, cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${(animatedProgress.value * 100).toInt()}%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.W800,
                                lineHeight = 13.sp,
                                color = if (isComplete) IncomeGreen else cs.onSurface,
                                fontFamily = InterFontFamily
                            )
                            Text(
                                "saved",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.W500,
                                lineHeight = 8.sp,
                                color = cs.onSurfaceVariant,
                                fontFamily = InterFontFamily
                            )
                        }
                    }

                    // ── Goal info ───────────────────────────────────────────────
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            goal.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W700,
                            color = cs.onSurface,
                            fontFamily = InterFontFamily,
                            modifier = Modifier.padding(bottom = 6.dp),
                            maxLines = 2
                        )
                        Row(
                            modifier = Modifier.padding(bottom = 8.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                String.format(Locale.US, "LKR %.0f", goal.savedAmount / 100.0),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W800,
                                color = cs.onSurface,
                                letterSpacing = (-0.5).sp,
                                fontFamily = InterFontFamily
                            )
                            Text(
                                String.format(Locale.US, " of LKR %.0f", goal.targetAmount / 100.0),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.W400,
                                color = cs.onSurfaceVariant,
                                fontFamily = InterFontFamily,
                                modifier = Modifier.padding(bottom = 1.dp)
                            )
                        }
                        // Progress bar (gradient fill)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(cs.outlineVariant, RoundedCornerShape(99.dp))
                        ) {
                            val fillFraction = animatedBarProgress.value.coerceIn(0f, 1f)
                            if (fillFraction > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fillFraction)
                                        .fillMaxHeight()
                                        .background(
                                            if (isComplete) Brush.horizontalGradient(listOf(GreenAccent, IncomeGreen))
                                            else Brush.horizontalGradient(listOf(GreenDeep, GreenAccent)),
                                            RoundedCornerShape(99.dp)
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Stats row (full-width border-top separator) ──────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(cs.outlineVariant)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Needed/mo
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    String.format(Locale.US, "LKR %.0f", monthlyNeeded / 100.0),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W700,
                    color = cs.onSurface,
                    fontFamily = InterFontFamily
                )
                Text(
                    "NEEDED/MO",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.W600,
                    color = cs.onSurfaceVariant,
                    letterSpacing = 0.5.sp,
                    fontFamily = InterFontFamily,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(cs.outlineVariant)
            )
            // Remaining
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "$monthsRemaining mo",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W700,
                    color = cs.onSurface,
                    fontFamily = InterFontFamily
                )
                Text(
                    "REMAINING",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.W600,
                    color = cs.onSurfaceVariant,
                    letterSpacing = 0.5.sp,
                    fontFamily = InterFontFamily,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(cs.outlineVariant)
            )
            // Projected date
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    targetDate,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W700,
                    color = cs.onSurface,
                    fontFamily = InterFontFamily
                )
                Text(
                    "PROJECTED",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.W600,
                    color = cs.onSurfaceVariant,
                    letterSpacing = 0.5.sp,
                    fontFamily = InterFontFamily,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
    }
}