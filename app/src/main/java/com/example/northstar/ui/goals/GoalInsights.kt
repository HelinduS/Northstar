package com.example.northstar.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.theme.*

@Composable
fun GoalInsightsRow(
    totalRemaining: Long,
    overallProgressPct: Int,
    completedCount: Int,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InsightTile(
            dot = Debit,
            label = "Remaining",
            value = "${totalRemaining / 100 / 1000}K",
            valueSuffix = "LKR",
            valueColor = Debit,
            sub = "to reach goals",
            modifier = Modifier.weight(1.1f)
        )

        Column(
            modifier = Modifier
                .weight(1.8f)
                .background(cs.surface, RoundedCornerShape(18.dp))
                .border(1.dp, cs.outline, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(cs.primary)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Overall",
                fontSize = 10.sp,
                fontWeight = FontWeight.W600,
                color = cs.onSurfaceVariant,
                letterSpacing = 0.5.sp,
                fontFamily = InterFontFamily
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                "$overallProgressPct%",
                fontSize = 22.sp,
                fontWeight = FontWeight.W900,
                color = cs.onSurface,
                letterSpacing = (-1).sp,
                fontFamily = InterFontFamily,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                "all goals",
                fontSize = 11.sp,
                color = cs.onSurfaceVariant,
                fontFamily = InterFontFamily
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(cs.outlineVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(
                            (overallProgressPct / 100f).coerceIn(0f, 1f)
                        )
                        .height(4.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(cs.primary)
                )
            }
        }

        InsightTile(
            dot = Credit,
            label = "Completed",
            value = "$completedCount",
            valueSuffix = "",
            valueColor = if (completedCount > 0) Credit else cs.onSurface,
            sub = "goals done",
            modifier = Modifier.weight(1.1f)
        )
    }
}

@Composable
private fun InsightTile(
    dot: Color,
    label: String,
    value: String,
    valueSuffix: String,
    valueColor: Color,
    sub: String,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .background(cs.surface, RoundedCornerShape(18.dp))
            .border(1.dp, cs.outline, RoundedCornerShape(18.dp))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dot)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            label,
            fontSize = 10.sp,
            fontWeight = FontWeight.W600,
            color = cs.onSurfaceVariant,
            letterSpacing = 0.3.sp,
            fontFamily = InterFontFamily,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(5.dp))
        if (valueSuffix.isNotBlank()) {
            Text(
                valueSuffix,
                fontSize = 9.sp,
                fontWeight = FontWeight.W500,
                color = valueColor.copy(alpha = 0.6f),
                fontFamily = InterFontFamily,
                lineHeight = 10.sp
            )
        }
        Text(
            value,
            fontSize = 18.sp,
            fontWeight = FontWeight.W900,
            color = valueColor,
            letterSpacing = (-0.8).sp,
            fontFamily = InterFontFamily,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            sub,
            fontSize = 10.sp,
            color = cs.onSurfaceVariant,
            fontFamily = InterFontFamily,
            lineHeight = 12.sp
        )
    }
}
