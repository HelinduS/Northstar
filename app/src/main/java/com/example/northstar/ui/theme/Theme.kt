package com.example.northstar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Navy900,
    onPrimary = White,
    secondary = IncomeGreen,
    error = Debit,
    background = Surface,
    surface = White,
    onSurface = TextPrimary,
    onBackground = TextPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = Navy900,
    onPrimary = White,
    secondary = IncomeGreen,
    error = Debit,
    background = Navy900,
    surface = Navy800,
    onSurface = White,
    onBackground = White
)

@Composable
fun NorthStarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}