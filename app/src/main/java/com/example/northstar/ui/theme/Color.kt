package com.example.northstar.ui.theme

import androidx.compose.ui.graphics.Color

// ── Green Finance Palette ─────────────────────────────────────────────────────
val GreenDeep    = Color(0xFF1B4332)   // dark forest green — hero card
val GreenMid     = Color(0xFF2D6A4F)   // medium green
val GreenAccent  = Color(0xFF52B788)   // positive amounts, accent
val GreenBright  = Color(0xFF74C69D)   // lighter accent
val GreenLight   = Color(0xFFD8F3DC)   // badge backgrounds, progress track
val GreenPale    = Color(0xFFF0F7F4)   // page background light mode

// ── Dark mode surfaces ─────────────────────────────────────────────────────────
val DarkPageBg   = Color(0xFF0A0A0A)   // near-black AMOLED
val DarkCard     = Color(0xFF121212)   // dark card
val DarkCardMid  = Color(0xFF1C1C1C)   // slightly lighter dark

// ── Semantic ──────────────────────────────────────────────────────────────────
val PositiveGreen = Color(0xFF52B788)
val NegativeRed   = Color(0xFFEF4444)
val NegativeRedDk = Color(0xFFFF6B6B)

// ── Text (light mode) ─────────────────────────────────────────────────────────
val TextPrimary   = Color(0xFF1A1A2E)
val TextSecondary = Color(0xFF6B7280)
val TextMuted     = Color(0xFF9CA3AF)
val TextHint      = Color(0xFFD1D5DB)

// ── Surfaces (light mode) ─────────────────────────────────────────────────────
val White       = Color(0xFFFFFFFF)
val Border      = Color(0xFFE8F0EC)
val ListDivider = Color(0xFFF0F7F4)
val Separator   = Color(0xFFF0F7F4)
val ChipBg      = GreenLight

// ── Legacy aliases (kept for files that still reference old names) ─────────────
val Navy900    = GreenDeep
val Navy800    = GreenMid
val Navy700    = Color(0xFF1a2332)
val Navy600    = Color(0xFF252F40)
val Navy500    = Color(0xFF3B4858)
val Surface    = GreenPale
val Credit     = PositiveGreen
val Debit      = NegativeRed
val IncomeGreen = GreenAccent
val ExpenseRed  = NegativeRedDk

val PrimaryBlue           = Color(0xFF3B5BDB)
val SecondaryAccentGreen  = Color(0xFF2F9E44)
val SemanticRed           = Color(0xFFE03131)
val NeutralWhite          = Color(0xFFFFFFFF)
val NeutralLightGrey      = Color(0xFFF2F4F7)
val NeutralCharcoal       = Color(0xFF0F1623)
val DashboardPageBackground = GreenPale
val DashboardSurface      = Color(0xFFFFFFFF)
val DashboardTextSecondary = Color(0xFF6B7A99)
val DashboardTextMuted    = Color(0xFFA0AABB)
val DashboardBorder       = Color(0x0A000000)
val DashboardCardBorder   = Color(0x14FFFFFF)
val DarkBackground        = DarkPageBg
val DarkSurface           = DarkCard
val DarkOnSurface         = Color(0xFFF8FAFC)
