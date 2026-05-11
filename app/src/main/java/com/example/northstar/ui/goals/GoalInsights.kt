package com.example.northstar.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Model ─────────────────────────────────────────────────────────────────────

data class InsightCard(
    val icon: ImageVector,
    val value: String,
    val label: String,
    val iconBg: Color,
    val iconTint: Color
)

// ── InsightChip ───────────────────────────────────────────────────────────────

@Composable
fun InsightChip(insight: InsightCard, modifier: Modifier = Modifier) {
    val cardBorder = Color(0xFFE1E4E8)
    val textPri    = Color(0xFF0D1117)
    val textMut    = Color(0xFF8E8E93)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(0.5.dp, cardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(insight.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    insight.icon,
                    contentDescription = null,
                    tint = insight.iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(insight.value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPri)
            Spacer(modifier = Modifier.height(2.dp))
            Text(insight.label, fontSize = 11.sp, color = textMut)
        }
    }
}