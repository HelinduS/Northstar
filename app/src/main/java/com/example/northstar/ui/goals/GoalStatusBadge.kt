package com.example.northstar.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.theme.PrimaryBlue
import com.example.northstar.ui.theme.SecondaryAccentGreen

@Composable
fun GoalStatusBadge(
    isReached: Boolean,
    progressPercent: Int,
    modifier: Modifier = Modifier
) {
    val safePercent = progressPercent.coerceIn(0, 100)
    val color = if (isReached) SecondaryAccentGreen else PrimaryBlue
    val text = if (isReached) "✓ Reached!" else "$safePercent%"

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}