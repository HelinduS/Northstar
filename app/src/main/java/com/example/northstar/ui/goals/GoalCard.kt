package com.example.northstar.ui.goals

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.domain.model.Goal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GoalCard(goal: Goal, viewModel: GoalViewModel) {
    val rawProgress = remember(goal.savedAmount, goal.targetAmount) {
        if (goal.targetAmount > 0L) (goal.savedAmount.toFloat() / goal.targetAmount.toFloat()).coerceIn(0f, 1f) else 0f
    }

    var progressVisible by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (progressVisible) rawProgress else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "progress"
    )
    LaunchedEffect(Unit) { progressVisible = true }

    val isReached      = viewModel.isGoalReached(goal)
    val percentComplete = (rawProgress * 100).toInt()

    // ── Design tokens ─────────────────────────────────────────────────────────
    val cardSurface        = Color(0xFFFFFFFF)
    val cardBorder         = Color(0x12000000)
    val textPrimary        = Color(0xFF111827)
    val textMuted          = Color(0xFF9CA3AF)
    val progressGreen      = Color(0xFF16A34A)
    val progressGreenEnd   = Color(0xFF4ADE80)
    val reachedBadgeBg     = Color(0xFFE8F5E9)
    val reachedBadgeBorder = Color(0xFFA3E9C9)
    val reachedBadgeText   = Color(0xFF16A34A)
    val dividerColor       = Color(0xFFF3F4F6)
    val deleteBg           = Color(0xFFFFF5F5)
    val deleteBorder       = Color(0xFFFFC9C9)
    val deleteText         = Color(0xFFDC2626)
    val addBg              = Color(0xFF0D1117)
    // ─────────────────────────────────────────────────────────────────────────

    val currencyFormat = remember { NumberFormat.getInstance(Locale.getDefault()) }
    val dateFormatter  = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val date           = if (goal.targetDate > 0L) dateFormatter.format(Date(goal.targetDate)) else "No date set"

    val daysRemaining = if (goal.targetDate > 0L) {
        val diff = goal.targetDate - System.currentTimeMillis()
        (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
    } else null

    var showContributeDialog by remember { mutableStateOf(false) }
    var showDeleteDialog     by remember { mutableStateOf(false) }
    var showReachedDialog    by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardSurface)
            .border(0.5.dp, cardBorder, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column {

            // Name + Reached badge
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(text = goal.name.replaceFirstChar { it.uppercase() }, fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold, color = textPrimary, modifier = Modifier.weight(1f))
                if (isReached) {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(reachedBadgeBg)
                            .border(0.5.dp, reachedBadgeBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("✓ Reached", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = reachedBadgeText)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Date
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = null, tint = textMuted, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = date, fontSize = 12.sp, color = textMuted)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount hero
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text(text = "LKR ${currencyFormat.format(goal.savedAmount / 100)}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                Text(text = "of LKR ${currencyFormat.format(goal.targetAmount / 100)}", fontSize = 13.sp, color = textMuted)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(100.dp)).background(Color(0xFFE5E7EB))) {
                Box(
                    modifier = Modifier.fillMaxWidth(animatedProgress).height(8.dp).clip(RoundedCornerShape(100.dp))
                        .background(Brush.horizontalGradient(colors = listOf(progressGreen, progressGreenEnd)))
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Micro labels
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$percentComplete% complete", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = progressGreen)
                daysRemaining?.let {
                    Text(if (isReached) "Goal achieved!" else "$it days to go", fontSize = 11.sp, color = textMuted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(dividerColor))
            Spacer(modifier = Modifier.height(14.dp))

            // Action row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { if (isReached) showReachedDialog = true else showContributeDialog = true },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = addBg, contentColor = Color.White),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Add funds", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Box(
                    modifier = Modifier.weight(1f).height(40.dp).clip(RoundedCornerShape(12.dp))
                        .background(deleteBg).border(0.5.dp, deleteBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = { showDeleteDialog = true }, modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(0.dp)) {
                        Text("Delete", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = deleteText)
                    }
                }
            }
        }
    }

    // ── Dialogs ───────────────────────────────────────────────────────────────

    if (showContributeDialog) {
        ContributeDialog(onDismiss = { showContributeDialog = false }) { amount ->
            viewModel.contributeToGoal(goal, amount)
            showContributeDialog = false
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text("Delete Goal", fontWeight = FontWeight.SemiBold) },
            text  = { Text("Are you sure you want to delete \"${goal.name}\"?", color = textMuted) },
            confirmButton = {
                Button(onClick = { viewModel.deleteGoal(goal.id); showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(10.dp)) { Text("Delete") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }, shape = RoundedCornerShape(10.dp)) { Text("Cancel") }
            }
        )
    }

    if (showReachedDialog) {
        AlertDialog(
            onDismissRequest = { showReachedDialog = false },
            shape = RoundedCornerShape(20.dp),
            icon = {
                Icon(imageVector = Icons.Outlined.CheckCircle, contentDescription = null,
                    tint = Color(0xFF16A34A), modifier = Modifier.size(36.dp))
            },
            title = { Text("Goal Reached!", fontWeight = FontWeight.SemiBold) },
            text  = { Text("You've already completed \"${goal.name}\"! No more savings needed.", color = textMuted) },
            confirmButton = {
                Button(onClick = { showReachedDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D1117)),
                    shape = RoundedCornerShape(10.dp)) { Text("Great!") }
            }
        )
    }
}