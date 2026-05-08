package com.example.northstar.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.northstar.ui.theme.PrimaryBlue
import com.example.northstar.ui.theme.SecondaryAccentGreen

@Composable
fun GoalProgressBar(progress: Float, isReached: Boolean) {
    val boundedProgress = progress.coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color.Gray.copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(boundedProgress)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(if (isReached) SecondaryAccentGreen else PrimaryBlue)
        )
    }
}