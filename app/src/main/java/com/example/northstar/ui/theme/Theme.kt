package com.example.northstar.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    secondary = SecondaryAccentGreen,
    error = SemanticRed,
    background = NeutralLightGrey,
    surface = Color.White,
    onSurface = NeutralCharcoal,
    onBackground = NeutralCharcoal
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    secondary = SecondaryAccentGreen,
    error = SemanticRed,
    background = DarkBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    onBackground = DarkOnSurface
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