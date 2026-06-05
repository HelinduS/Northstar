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
import com.example.northstar.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GoalCard(goal: Goal, viewModel: GoalViewModel) {
    val cs = MaterialTheme.colorScheme
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

    val isReached       = viewModel.isGoalReached(goal)
    val percentComplete = (rawProgress * 100).toInt()

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
    var showEditDialog       by remember { mutableStateOf(false) } // NEW

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cs.surface)
            .border(1.dp, cs.outline, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column {

            // Name + Reached badge
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(text = goal.name.replaceFirstChar { it.uppercase() }, fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold, color = cs.onSurface, modifier = Modifier.weight(1f),
                    fontFamily = InterFontFamily, letterSpacing = (-0.2).sp)
                if (isReached) {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Color(0xFFEBFBEE))
                            .border(1.dp, Color(0xFFA3E9C9), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("✓ Reached", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Credit, fontFamily = InterFontFamily)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Date
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = null, tint = cs.onSurfaceVariant, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = date, fontSize = 12.sp, color = cs.onSurfaceVariant, fontFamily = InterFontFamily)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount hero
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text(text = "LKR ${currencyFormat.format(goal.savedAmount / 100)}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = cs.onSurface, fontFamily = InterFontFamily)
                Text(text = "of LKR ${currencyFormat.format(goal.targetAmount / 100)}", fontSize = 13.sp, color = cs.onSurfaceVariant, fontFamily = InterFontFamily)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(100.dp)).background(cs.outlineVariant)) {
                Box(
                    modifier = Modifier.fillMaxWidth(animatedProgress).height(8.dp).clip(RoundedCornerShape(100.dp))
                        .background(Brush.horizontalGradient(colors = listOf(GreenDeep, GreenAccent)))
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Micro labels
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$percentComplete% complete", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GreenAccent, fontFamily = InterFontFamily)
                daysRemaining?.let {
                    Text(if (isReached) "Goal achieved!" else "$it days to go", fontSize = 11.sp, color = cs.onSurfaceVariant, fontFamily = InterFontFamily)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(cs.outlineVariant))
            Spacer(modifier = Modifier.height(14.dp))

            // ── Action row ────────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                // Add funds
                Button(
                    onClick = { if (isReached) showReachedDialog = true else showContributeDialog = true },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenDeep, contentColor = White),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Add funds", fontSize = 13.sp, fontWeight = FontWeight.Medium, fontFamily = InterFontFamily)
                }

                // Edit (NEW)
                Box(
                    modifier = Modifier
                        .weight(1f).height(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0FDF4))
                        .border(0.5.dp, Color(0xFFA3E9C9), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "Edit",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = GreenDeep,
                            fontFamily = InterFontFamily
                        )
                    }
                }

                // Delete
                Box(
                    modifier = Modifier
                        .weight(1f).height(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF5F5))
                        .border(0.5.dp, Color(0xFFFFC9C9), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Delete", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Debit, fontFamily = InterFontFamily)
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
            title = { Text("Delete Goal", fontWeight = FontWeight.SemiBold, fontFamily = InterFontFamily) },
            text  = { Text("Are you sure you want to delete \"${goal.name}\"?", color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = InterFontFamily) },
            confirmButton = {
                Button(onClick = { viewModel.deleteGoal(goal.id); showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(10.dp)) { Text("Delete", fontFamily = InterFontFamily) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }, shape = RoundedCornerShape(10.dp)) { Text("Cancel", fontFamily = InterFontFamily) }
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
            title = { Text("Goal Reached!", fontWeight = FontWeight.SemiBold, fontFamily = InterFontFamily) },
            text  = { Text("You've already completed \"${goal.name}\"! No more savings needed.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = InterFontFamily) },
            confirmButton = {
                Button(onClick = { showReachedDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenDeep),
                    shape = RoundedCornerShape(10.dp)) { Text("Great!", fontFamily = InterFontFamily) }
            }
        )
    }

    // NEW: Edit dialog
    if (showEditDialog) {
        EditGoalDialog(
            goal      = goal,
            onDismiss = { showEditDialog = false },
            onConfirm = { newName, newAmount, newDate ->
                viewModel.updateGoal(
                    goalId          = goal.id,
                    newName         = newName,
                    newTargetAmount = newAmount,
                    newTargetDate   = newDate
                )
                showEditDialog = false
            }
        )
    }
}