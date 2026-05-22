package com.example.northstar.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.theme.*
import java.util.*

@Composable
fun TodaysSpendCard(todaysExpense: Long) {
    val cs = MaterialTheme.colorScheme

    Column(modifier = Modifier.padding(top = 20.dp)) {
        // ── Section header ────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Today's Spend",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = cs.onSurface,
                fontFamily = InterFontFamily
            )
            Text(
                java.text.SimpleDateFormat("MMM dd", Locale.US).format(java.util.Date()),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = cs.onSurfaceVariant,
                fontFamily = InterFontFamily
            )
        }

        // ── Card ──────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(horizontal = 20.dp)
                .background(cs.surface, RoundedCornerShape(20.dp))
                .border(1.dp, cs.outline, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
        ) {
            // Left accent bar — red for spending
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            listOf(NegativeRed, NegativeRed.copy(alpha = 0.5f))
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "SPENT TODAY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = cs.onSurfaceVariant,
                        letterSpacing = 0.5.sp,
                        fontFamily = InterFontFamily
                    )
                    Text(
                        String.format(Locale.US, "LKR %.2f", todaysExpense / 100.0),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = cs.onSurface,
                        letterSpacing = (-1).sp,
                        fontFamily = InterFontFamily,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(NegativeRed.copy(alpha = 0.10f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.TrendingDown,
                        contentDescription = null,
                        tint = NegativeRed,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
