package com.example.northstar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary             = GreenDeep,
    onPrimary           = White,
    primaryContainer    = GreenLight,
    onPrimaryContainer  = GreenDeep,
    secondary           = GreenAccent,
    onSecondary         = White,
    secondaryContainer  = GreenLight,
    onSecondaryContainer = GreenDeep,
    background          = GreenPale,
    onBackground        = TextPrimary,
    surface             = White,
    onSurface           = TextPrimary,
    surfaceVariant      = GreenPale,
    onSurfaceVariant    = TextSecondary,
    outline             = Border,
    outlineVariant      = ListDivider,
    error               = NegativeRed,
    onError             = White
)

private val DarkColors = darkColorScheme(
    primary             = GreenAccent,
    onPrimary           = GreenDeep,
    primaryContainer    = GreenDeep,
    onPrimaryContainer  = Color(0xFFD8F3DC),
    secondary           = GreenBright,
    onSecondary         = GreenDeep,
    secondaryContainer  = DarkCardMid,
    onSecondaryContainer = Color(0xFFD8F3DC),
    background          = DarkPageBg,
    onBackground        = Color(0xFFF0F0F0),
    surface             = DarkCard,
    onSurface           = Color(0xFFF0F0F0),   // near-white — primary text in dark mode
    surfaceVariant      = DarkCardMid,
    onSurfaceVariant    = Color(0xFFAAAAAA),   // neutral gray — secondary/muted text
    outline             = Color(0xFF2A2A2A),
    outlineVariant      = Color(0xFF1E1E1E),
    error               = NegativeRedDk,
    onError             = GreenDeep
)

@Composable
fun NorthStarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colorScheme.background
        ) {
            content()
        }
    }
}
