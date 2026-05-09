package com.example.northstar.ui.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.theme.*

@Composable
fun SavingsGoalCard() {
    Column(modifier = Modifier.padding(top = 20.dp)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Savings Goal", fontSize = 15.sp, fontWeight = FontWeight.W700, color = TextPrimary, fontFamily = InterFontFamily)
            Text("Edit", fontSize = 11.sp, fontWeight = FontWeight.W600, color = TextSecondary, fontFamily = InterFontFamily)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(White, RoundedCornerShape(20.dp))
                .border(1.dp, Border, RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // ── Goal ring ──
                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(80.dp)) {
                            val stroke = 8.dp.toPx()
                            val inset = stroke / 2f
                            val arcSize = Size(size.width - stroke, size.height - stroke)
                            val topLeft = Offset(inset, inset)

                            // track
                            drawArc(
                                color = Separator,
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = stroke)
                            )
                            // progress — 9.8% of 360 = 35.28°
                            drawArc(
                                color = Navy900,
                                startAngle = -90f,
                                sweepAngle = 35.28f,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = stroke, cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "9.8%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W800,
                                color = TextPrimary,
                                fontFamily = InterFontFamily
                            )
                            Text(
                                "saved",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.W500,
                                color = TextMuted,
                                fontFamily = InterFontFamily
                            )
                        }
                    }

                    // ── Goal info ──
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "MacBook Pro M4",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.W700,
                            color = TextPrimary,
                            fontFamily = InterFontFamily,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(
                            modifier = Modifier.padding(bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "LKR 48,200",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W800,
                                color = TextPrimary,
                                letterSpacing = (-0.6).sp,
                                fontFamily = InterFontFamily
                            )
                            Text(
                                "  of LKR 490,000",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.W500,
                                color = TextMuted,
                                fontFamily = InterFontFamily
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .background(Separator, RoundedCornerShape(99.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.098f)
                                    .height(5.dp)
                                    .background(Navy900, RoundedCornerShape(99.dp))
                            )
                        }
                    }
                }

                HorizontalDivider(
                    color = Separator,
                    thickness = 1.dp,
                    modifier = Modifier.padding(top = 14.dp, bottom = 14.dp)
                )

                // ── Stat cells with vertical dividers ──
                Row(modifier = Modifier.fillMaxWidth()) {
                    GoalStatCell(
                        value = "LKR 36,817",
                        label = "NEEDED / MO",
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(Separator)
                    )
                    GoalStatCell(
                        value = "11 months",
                        label = "REMAINING",
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(Separator)
                    )
                    GoalStatCell(
                        value = "Apr 2027",
                        label = "PROJECTED",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}