package com.example.northstar.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.northstar.R

// Define Google Fonts using downloadable fonts
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val PlusJakartaSans = GoogleFont("Plus Jakarta Sans")

val AppFontFamily = FontFamily(
    Font(googleFont = PlusJakartaSans, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = PlusJakartaSans, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = PlusJakartaSans, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = PlusJakartaSans, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = PlusJakartaSans, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = PlusJakartaSans, fontProvider = provider, weight = FontWeight.ExtraBold)
)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 57.sp, lineHeight = 64.sp),
    headlineLarge = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp),
    titleLarge = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    bodyLarge = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    headlineMedium = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp),
    titleMedium = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp)
)