package com.example.northstar.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.domain.model.Goal
import com.example.northstar.ui.theme.PrimaryBlue
import com.example.northstar.ui.theme.NeutralWhite

@Composable
fun ActiveGoalCard(goal: Goal, viewModel: GoalViewModel) {
    val progress = viewModel.getProgress(goal) / 100f
    val remaining = viewModel.getRemainingAmount(goal)
    val progressPercent = viewModel.getProgress(goal).toInt()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF3B66B9), // A lighter tint of your PrimaryBlue for the top
                        PrimaryBlue        // Your brand PrimaryBlue (0xFF103986) at the bottom
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Active Goal",
                        fontSize = 12.sp,
                        color = NeutralWhite.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = goal.name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralWhite
                    )
                }

                // Translucent Badge using Brand Neutral White
                Surface(
                    color = NeutralWhite.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "$progressPercent%",
                        color = NeutralWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Progress Bar using Neutral White
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NeutralWhite.copy(alpha = 0.25f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .height(6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeutralWhite)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Saved",
                        fontSize = 12.sp,
                        color = NeutralWhite.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "LKR ${goal.savedAmount / 100}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeutralWhite
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Remaining",
                        fontSize = 12.sp,
                        color = NeutralWhite.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "LKR ${remaining / 100}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeutralWhite
                    )
                }
            }
        }
    }
}