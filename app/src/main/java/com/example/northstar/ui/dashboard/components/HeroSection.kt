package com.example.northstar.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HeroSection(
    displayName: String,
    totalBalance: Long,
    income: Long,
    expenses: Long
) {
    // Outer box — full navy background including the overlap area
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Navy900)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {

            // ── Header row ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Navy800)
                        .border(1.5.dp, White.copy(alpha = 0.08f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "HS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W700,
                        color = White,
                        fontFamily = InterFontFamily
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 11.dp)
                ) {
                    Text(
                        "Good evening,",
                        fontSize = 11.sp,
                        color = White.copy(alpha = 0.38f),
                        fontFamily = InterFontFamily
                    )
                    Text(
                        displayName.ifBlank { "User" },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W700,
                        color = White,
                        letterSpacing = (-0.3).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = InterFontFamily
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.06f))
                        .border(1.dp, White.copy(alpha = 0.08f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = White.copy(alpha = 0.7f)
                    )
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                            .border(1.5.dp, Navy900, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }

            // ── Balance card ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White.copy(alpha = 0.06f), RoundedCornerShape(20.dp))
                    .border(1.dp, White.copy(alpha = 0.09f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Total Balance",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W500,
                            color = White.copy(alpha = 0.45f),
                            fontFamily = InterFontFamily
                        )
                        Box(
                            modifier = Modifier
                                .background(White.copy(alpha = 0.1f), RoundedCornerShape(99.dp))
                                .border(1.dp, White.copy(alpha = 0.12f), RoundedCornerShape(99.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(IncomeGreen)
                                )
                                Text(
                                    SimpleDateFormat("MMMM yyyy", Locale.US).format(Date()),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.W600,
                                    color = White.copy(alpha = 0.75f),
                                    fontFamily = InterFontFamily
                                )
                            }
                        }
                    }

                    val balance = totalBalance / 100.0
                    Text(
                        buildAnnotatedString {
                            append("LKR ")
                            append(String.format(Locale.US, "%.2f", balance))
                        },
                        fontSize = 38.sp,
                        fontWeight = FontWeight.W800,
                        color = White,
                        letterSpacing = (-2).sp,
                        modifier = Modifier.padding(top = 12.dp, bottom = 18.dp),
                        fontFamily = InterFontFamily
                    )

                    HorizontalDivider(
                        color = White.copy(alpha = 0.08f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Income",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.W500,
                                color = IncomeGreen,
                                fontFamily = InterFontFamily,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            Text(
                                String.format(Locale.US, "LKR %.2f", income / 100.0),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.W700,
                                color = White,
                                letterSpacing = (-0.5).sp,
                                fontFamily = InterFontFamily
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(42.dp)
                                .align(Alignment.CenterVertically)
                                .background(White.copy(alpha = 0.08f))
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                        ) {
                            Text(
                                "Expenses",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.W500,
                                color = ExpenseRed,
                                fontFamily = InterFontFamily,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            Text(
                                String.format(Locale.US, "LKR %.2f", expenses / 100.0),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.W700,
                                color = White,
                                letterSpacing = (-0.5).sp,
                                fontFamily = InterFontFamily
                            )
                        }
                    }
                }
            }

            // gap between card and the white rounded cap
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ── White rounded cap — sits at bottom of the navy Box ──
        // Creates the illusion of the white sheet sliding up over the dark hero
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Surface,
                    RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
        ) {
            // Pull handle
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 4.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 0.dp)
                    .background(
                        Color.Black.copy(alpha = 0.1f),
                        RoundedCornerShape(99.dp)
                    )
            )
        }
    }
}