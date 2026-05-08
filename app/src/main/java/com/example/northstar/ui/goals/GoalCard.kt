package com.example.northstar.ui.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.domain.model.Goal
import com.example.northstar.ui.theme.PrimaryBlue
import com.example.northstar.ui.theme.SemanticRed
import com.example.northstar.ui.theme.SecondaryAccentGreen
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GoalCard(goal: Goal, viewModel: GoalViewModel) {
    // Logic & State
    val progress = remember(goal.savedAmount, goal.targetAmount) {
        if (goal.targetAmount > 0L) (goal.savedAmount.toFloat() / goal.targetAmount.toFloat()).coerceIn(0f, 1f) else 0f
    }
    val isReached = viewModel.isGoalReached(goal)
    val accentColor = if (isReached) SecondaryAccentGreen else Color.White

    var showContributeDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            GoalCardHeader(goal, viewModel.getProgress(goal).toInt(), isReached, accentColor)

            Spacer(modifier = Modifier.height(20.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = accentColor,
                trackColor = Color.White.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(20.dp))

            GoalCardFooter(
                goal = goal,
                onAdd = { showContributeDialog = true },
                onDelete = { showDeleteDialog = true }
            )
        }
    }

    // Dialog Logic
    if (showContributeDialog) {
        ContributeDialog(onDismiss = { showContributeDialog = false }) { amount ->
            viewModel.contributeToGoal(goal, amount)
            showContributeDialog = false
        }
    }
    if (showDeleteDialog) {
        DeleteGoalDialog(goal.name, onDismiss = { showDeleteDialog = false }) {
            viewModel.deleteGoal(goal.id)
            showDeleteDialog = false
        }
    }
}

@Composable
private fun GoalCardHeader(goal: Goal, percent: Int, isReached: Boolean, accent: Color) {
    val date = if (goal.targetDate > 0L) SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(goal.targetDate)) else "No date set"
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(goal.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
            Text("Target: $date", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
        }
        Surface(color = accent.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)) {
            Text(
                text = if (isReached) "Reached" else "$percent%",
                color = accent,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GoalCardFooter(goal: Goal, onAdd: () -> Unit, onDelete: () -> Unit) {
    val currencyFormat = NumberFormat.getInstance(Locale.getDefault())
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
        Column {
            Text("LKR ${currencyFormat.format(goal.savedAmount / 100)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("of LKR ${currencyFormat.format(goal.targetAmount / 100)}", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionButton("Add", Color.White.copy(alpha = 0.2f), Color.White, onAdd)
            ActionButton("Delete", SemanticRed, Color.White, onDelete)
        }
    }
}

@Composable
private fun ActionButton(text: String, container: Color, content: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = content),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text, fontSize = 12.sp)
    }
}

@Composable
private fun DeleteGoalDialog(name: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Delete Goal", fontWeight = FontWeight.Bold) },
        text = { Text("Are you sure you want to delete \"$name\"?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = SemanticRed),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Delete") }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE), contentColor = Color.Black)
            ) { Text("Cancel") }
        }
    )
}