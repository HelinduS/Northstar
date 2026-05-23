package com.example.northstar.ui.dashboard.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.northstar.ui.theme.*
import java.util.*

@Composable
fun HeroSection(
    displayName: String,
    totalBalance: Long,
    income: Long,
    expenses: Long,
    allTimeBalance: Long = totalBalance,
    allTimeIncome: Long = income,
    allTimeExpenses: Long = expenses,
    greetingText: String = "Good morning,",
    greetingIcon: ImageVector = Icons.Filled.WbSunny,
    unreadNotificationCount: Int = 0,
    onNotificationClick: () -> Unit = {}
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        DisposableEffect(Unit) {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false  // white icons on dark green hero
            onDispose {
                controller.isAppearanceLightStatusBars = true  // restore for other screens
            }
        }
    }

    val initials = displayName
        .split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifBlank { "?" }

    val spendRatio = if (income > 0) (expenses.toFloat() / income).coerceIn(0f, 1f) else 0f
    val spendPct   = (spendRatio * 100).toInt()

    val heroBrush = Brush.linearGradient(
        colors = listOf(GreenDeep, GreenMid),
        start  = Offset(0f, 0f),
        end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(heroBrush, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 22.dp)
    ) {
        // ── Greeting row ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(GreenAccent.copy(alpha = 0.25f), CircleShape)
                    .border(1.5.dp, GreenAccent.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Navy800)
                        .border(1.5.dp, White.copy(alpha = 0.08f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        displayName
                            .split(" ")
                            .filter { it.isNotBlank() }
                            .take(2)
                            .joinToString("") { it.first().uppercaseChar().toString() }
                            .ifBlank { "?" },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W700,
                        color = White,
                        fontFamily = InterFontFamily
                    )
                }
            }

            // Greeting + Name
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 11.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = greetingIcon,
                        contentDescription = null,
                        tint = White.copy(alpha = 0.55f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = greetingText,
                        fontSize = 11.sp,
                        color = White.copy(alpha = 0.38f),
                        fontFamily = InterFontFamily
                    )
                }
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

            // ── Bell icon with badge overlapping top-right ──
            Box(modifier = Modifier.size(48.dp)) {
                // Bell button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.06f))
                        .border(1.dp, White.copy(alpha = 0.08f), CircleShape)
                        .clickable { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        modifier = Modifier.size(18.dp),
                        tint = White.copy(alpha = 0.7f)
                    )
                }

                // Red badge overlapping top-right corner
                if (unreadNotificationCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(if (unreadNotificationCount > 9) 16.dp else 12.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                            .border(1.5.dp, Navy900, CircleShape)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        if (unreadNotificationCount > 9) {
                            Text(
                                "9+",
                                color = Color.White,
                                fontSize = 6.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        greetingIcon,
                        contentDescription = null,
                        tint = White.copy(alpha = 0.55f),
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        greetingText,
                        fontSize = 11.sp,
                        color = White.copy(alpha = 0.55f),
                        fontFamily = InterFontFamily
                    )
                }
                Text(
                    displayName.ifBlank { "User" },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = InterFontFamily
                )
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(White.copy(alpha = 0.12f), CircleShape)
                    .border(1.dp, White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.NotificationsNone,
                    contentDescription = null,
                    modifier = Modifier.size(19.dp),
                    tint = White
                )
            }
        }

        // ── NET SAVED label ───────────────────────────────────────────────────
        Text(
            "NET SAVED",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = White.copy(alpha = 0.55f),
            letterSpacing = 1.sp,
            fontFamily = InterFontFamily
        )

        // ── Primary balance number ────────────────────────────────────────────
        Text(
            String.format(Locale.US, "LKR %.2f", allTimeBalance / 100.0),
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = White,
            letterSpacing = (-1.5).sp,
            lineHeight = 36.sp,           // matches prototype's line-height: 1
            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp),
            fontFamily = InterFontFamily
        )

        // ── Income / Expenses stats row ───────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(White.copy(alpha = 0.10f), RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "INCOME",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = White.copy(alpha = 0.5f),
                    letterSpacing = 0.5.sp,
                    lineHeight = 13.sp,
                    fontFamily = InterFontFamily
                )
                Text(
                    String.format(Locale.US, "LKR %.2f", allTimeIncome / 100.0),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenBright,
                    letterSpacing = (-0.3).sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 3.dp),
                    fontFamily = InterFontFamily
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(36.dp)
                    .background(White.copy(alpha = 0.2f))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    "EXPENSES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = White.copy(alpha = 0.5f),
                    letterSpacing = 0.5.sp,
                    lineHeight = 13.sp,
                    fontFamily = InterFontFamily
                )
                Text(
                    String.format(Locale.US, "LKR %.2f", allTimeExpenses / 100.0),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF8A80),
                    letterSpacing = (-0.3).sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 3.dp),
                    fontFamily = InterFontFamily
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // ── Spend progress bar ────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "0%",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = White.copy(alpha = 0.6f),
                fontFamily = InterFontFamily
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(White.copy(alpha = 0.18f))
            ) {
                if (spendRatio > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(spendRatio)
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(listOf(GreenAccent, GreenBright)),
                                RoundedCornerShape(99.dp)
                            )
                    )
                }
            }
            Text(
                "$spendPct% spent",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = White.copy(alpha = 0.6f),
                fontFamily = InterFontFamily
            )
        }
    }
}
