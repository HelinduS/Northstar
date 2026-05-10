package com.example.northstar.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Model ─────────────────────────────────────────────────────────────────────

data class GoalTemplate(
    val icon: ImageVector,
    val label: String,
    val sub: String,
    val iconBg: Color,
    val iconTint: Color,
    val targetAmount: Double? = null
)

// ── Color tokens (template-specific) ─────────────────────────────────────────

private val Blue         = Color(0xFF2979FF)
private val Indigo       = Color(0xFF3B5BDB)
private val Purple       = Color(0xFF7048E8)
private val GreenStart   = Color(0xFF1A9C5B)
private val OrangeTint   = Color(0xFFF57C00)
private val OrangeBg     = Color(0xFFFFF3E0)
private val PinkAccent   = Color(0xFFE91E63)
private val PinkBg       = Color(0xFFFCE4EC)
private val CyanAccent   = Color(0xFF00BCD4)
private val CyanBg       = Color(0xFFE0F7FA)
private val VioletAccent = Color(0xFF9C27B0)
private val VioletBg     = Color(0xFFF3E5F5)
private val IndigoBg     = Color(0xFFE8EAF6)
private val GreenBg      = Color(0xFFE8F5E9)
private val PurpleBg     = Color(0xFFEDE7F6)
private val BlueBg       = Color(0xFFE3F2FD)
private val RoseAccent   = Color(0xFFE91E8C)
private val RoseBg       = Color(0xFFFFF0F6)

// ── All templates ─────────────────────────────────────────────────────────────

val allGoalTemplates = listOf(
    GoalTemplate(Icons.Outlined.HealthAndSafety, "Emergency Fund", "Safety net",     GreenBg,   GreenStart,   targetAmount = 300000.0),
    GoalTemplate(Icons.Outlined.Flight,          "Vacation",       "Dream trip",     BlueBg,    Blue,         targetAmount = 150000.0),
    GoalTemplate(Icons.Outlined.DirectionsCar,   "New Car",        "Drive in style", OrangeBg,  OrangeTint,   targetAmount = 2000000.0),
    GoalTemplate(Icons.Outlined.Home,            "Dream Home",     "Own it",         PurpleBg,  Purple,       targetAmount = 10000000.0),
    GoalTemplate(Icons.Outlined.Devices,         "Gadget",         "Tech upgrade",   IndigoBg,  Indigo,       targetAmount = 100000.0),
    GoalTemplate(Icons.Outlined.School,          "Education",      "Invest in self", PinkBg,    PinkAccent,   targetAmount = 500000.0),
    GoalTemplate(Icons.Outlined.ChildCare,       "Baby Fund",      "Family first",   VioletBg,  VioletAccent, targetAmount = 250000.0),
    GoalTemplate(Icons.Outlined.BusinessCenter,  "Business",       "Launch it",      CyanBg,    CyanAccent,   targetAmount = 1000000.0),
    GoalTemplate(Icons.Outlined.Favorite,        "Wedding",        "Big day fund",   RoseBg,    RoseAccent,   targetAmount = 750000.0)
)

// ── GoalTemplateCard ──────────────────────────────────────────────────────────

@Composable
fun GoalTemplateCard(
    template: GoalTemplate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBorder = Color(0xFFE1E4E8)
    val textPri    = Color(0xFF0D1117)
    val textMut    = Color(0xFF8E8E93)

    Box(
        modifier = modifier
            .width(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(0.5.dp, cardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(template.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    template.icon,
                    contentDescription = null,
                    tint = template.iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                template.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = textPri,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                template.sub,
                fontSize = 10.sp,
                color = textMut,
                textAlign = TextAlign.Center
            )
        }
    }
}