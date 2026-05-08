package com.example.northstar.ui.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.domain.model.Goal
import com.example.northstar.ui.theme.NeutralCharcoal
import com.example.northstar.ui.theme.NeutralLightGrey
import com.example.northstar.ui.theme.PrimaryBlue
import com.example.northstar.ui.theme.SemanticRed

@Composable
fun GoalCard(goal: Goal, viewModel: GoalViewModel) {
    val progress = viewModel.getProgress(goal) / 100f
    val progressPercent = viewModel.getProgress(goal).toInt()
    val isReached = viewModel.isGoalReached(goal)
    var showContributeDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralLightGrey),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NeutralCharcoal
                )
                GoalStatusBadge(isReached = isReached, progressPercent = progressPercent)
            }

            Spacer(modifier = Modifier.height(12.dp))
            GoalProgressBar(progress = progress, isReached = isReached)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LKR ${goal.savedAmount / 100}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeutralCharcoal
                    )
                    Text(
                        text = "of LKR ${goal.targetAmount / 100}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { showContributeDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(PrimaryBlue)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) { Text("Add", fontSize = 12.sp) }
                    OutlinedButton(
                        onClick = { viewModel.deleteGoal(goal.id) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SemanticRed),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(SemanticRed)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) { Text("Delete", fontSize = 12.sp) }
                }
            }
        }
    }

    if (showContributeDialog) {
        ContributeDialog(
            onDismiss = { showContributeDialog = false },
            onConfirm = { amount ->
                viewModel.contributeToGoal(goal, amount)
                showContributeDialog = false
            }
        )
    }
}