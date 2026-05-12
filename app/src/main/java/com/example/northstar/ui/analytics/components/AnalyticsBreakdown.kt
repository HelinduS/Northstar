package com.example.northstar.ui.analytics.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.domain.model.CategoryBreakdown
import com.example.northstar.ui.theme.*

@Composable
fun AnalyticsBreakdown(item: CategoryBreakdown) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(8.dp), shape = CircleShape, color = item.color) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text(item.categoryName, fontWeight = FontWeight.Medium, color = TextPrimary, fontSize = 14.sp)
            }
            Text("Rs.${item.totalAmount / 100}", fontWeight = FontWeight.SemiBold, color = Navy900, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { item.percentage },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = item.color,
            trackColor = item.color.copy(alpha = 0.1f),
            strokeCap = StrokeCap.Round
        )
    }
}