package com.example.northstar.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
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
fun ThisMonthCard(income: Long, expenses: Long) {
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
                "Month at a Glance",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = cs.onSurface,
                fontFamily = InterFontFamily
            )
            Text(
                java.text.SimpleDateFormat("MMMM yyyy", Locale.US).format(Date()),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = cs.onSurfaceVariant,
                fontFamily = InterFontFamily
            )
        }

        // ── Card ──────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(cs.surface, RoundedCornerShape(20.dp))
                .border(1.dp, cs.outline, RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Column {
            // ── Top row: Net saved + sparkline ────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "NET SAVED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = cs.onSurfaceVariant,
                        letterSpacing = 0.5.sp,
                        fontFamily = InterFontFamily
                    )
                    val balance = (income - expenses) / 100.0
                    Text(
                        String.format(Locale.US, "LKR %.2f", balance),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = cs.onSurface,
                        letterSpacing = (-0.8).sp,
                        fontFamily = InterFontFamily,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // ── Mini sparkline bars ───────────────────────────────────────
                val spendRatio = if (income > 0) (expenses.toFloat() / income).coerceIn(0.1f, 1f) else 0.3f
                val barHeights = remember(income, expenses) {
                    // Last bar = actual spend ratio; others vary around it for visual shape
                    listOf(0.35f, 0.55f, 0.40f, 0.70f, 0.45f, 0.60f, spendRatio)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.height(40.dp)
                ) {
                    barHeights.forEachIndexed { index, fraction ->
                        val isLast = index == barHeights.lastIndex
                        Box(
                            modifier = Modifier
                                .width(7.dp)
                                .fillMaxHeight(fraction)
                                .background(
                                    if (isLast) GreenAccent
                                    else GreenDeep.copy(alpha = 0.45f),
                                    RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                                )
                        )
                    }
                }
            }

                // ── Legend ────────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(Modifier.size(6.dp).background(GreenAccent, RoundedCornerShape(99.dp)))
                        Text("Income", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurfaceVariant, fontFamily = InterFontFamily)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(Modifier.size(6.dp).background(NegativeRed, RoundedCornerShape(99.dp)))
                        Text("Expenses", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurfaceVariant, fontFamily = InterFontFamily)
                    }
                }

                // ── Split bar ─────────────────────────────────────────────────
                val total = income + expenses
                val incomeRatio = if (total > 0) income.toFloat() / total else 0f

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(cs.surfaceVariant)
                ) {
                    if (incomeRatio > 0f) {
                        Box(
                            modifier = Modifier
                                .weight(incomeRatio)
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(listOf(GreenDeep, GreenAccent)),
                                    RoundedCornerShape(99.dp)
                                )
                        )
                    }
                    if (incomeRatio < 1f) {
                        Box(
                            modifier = Modifier
                                .weight(1f - incomeRatio)
                                .fillMaxHeight()
                                .background(NegativeRed, RoundedCornerShape(99.dp))
                        )
                    }
                }

                // ── Amounts ───────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        String.format(Locale.US, "LKR %.2f", income / 100.0),
                        fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenAccent, fontFamily = InterFontFamily
                    )
                    Text(
                        String.format(Locale.US, "LKR %.2f", expenses / 100.0),
                        fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NegativeRed, fontFamily = InterFontFamily
                    )
                }
            }
        }
    }
}
