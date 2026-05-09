package com.example.northstar.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.northstar.ui.theme.*
import androidx.compose.ui.graphics.Color

@Composable
fun GoalStatCell(value: String, label: String, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.W700, color = TextPrimary, letterSpacing = (-0.3).sp, fontFamily = InterFontFamily)
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.W500, color = TextMuted, letterSpacing = 0.2.sp, fontFamily = InterFontFamily, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
fun SpendChip(dot: Color, label: String) {
    Box(modifier = Modifier.background(ChipBg, RoundedCornerShape(99.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(dot))
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.W600, color = TextPrimary, fontFamily = InterFontFamily)
        }
    }
}

