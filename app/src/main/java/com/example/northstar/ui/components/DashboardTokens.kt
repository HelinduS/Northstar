@file:Suppress("unused")

package com.example.northstar.ui.components

import androidx.compose.ui.graphics.Color
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.absoluteValue

internal val DashboardPageBackground = Color(0xFFF2F4F7)
internal val DashboardSurface = Color.White
internal val DashboardPrimary = Color(0xFF3B5BDB)
internal val DashboardPrimaryGradientEnd = Color(0xFF7048E8)
internal val DashboardTextPrimary = Color(0xFF0F1623)
internal val DashboardTextSecondary = Color(0xFF6B7A99)
internal val DashboardTextMuted = Color(0xFFA0AABB)
internal val DashboardSuccess = Color(0xFF2F9E44)
internal val DashboardSuccessSoft = Color(0xFF69DB7C)
internal val DashboardDestructive = Color(0xFFE03131)
internal val DashboardExpenseSoft = Color(0xFFFF8787)
internal val DashboardBorder = Color(0x0A000000)
internal val DashboardBorderSoft = Color(0x14FFFFFF)
internal val DashboardTrack = Color(0xFFF0F1F3)
internal val DashboardCommittedTile = Color(0xFFEEF2FF)
internal val DashboardDiscretionaryTile = Color(0xFFEBFBEE)
internal val DashboardRentTile = Color(0xFFFFF5F5)
internal val DashboardOtherTile = Color(0xFFFFF9DB)
internal val DashboardIncomeTile = Color(0xFFEEF2FF)

internal fun formatLkr(valueInPaisa: Long): String {
    val amount = valueInPaisa.absoluteValue / 100.0
    val prefix = if (valueInPaisa < 0) "-LKR " else "LKR "
    return prefix + String.format(Locale.US, "%,.2f", amount)
}

internal fun formatLkrPlain(valueInPaisa: Long): String {
    return String.format(Locale.US, "%,.2f", valueInPaisa.absoluteValue / 100.0)
}

internal fun percentLabel(fraction: Float): String = "${(fraction.coerceIn(0f, 1f) * 100).toInt()}%"

internal fun currentMonthLabel(): String =
    YearMonth.now().format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.US))

internal fun initials(displayName: String, email: String): String {
    val candidate = displayName.trim().ifBlank {
        email.substringBefore('@').replace('.', ' ').trim()
    }

    if (candidate.isBlank()) return "NS"

    return candidate
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifBlank { candidate.take(2).uppercase(Locale.US) }
}


