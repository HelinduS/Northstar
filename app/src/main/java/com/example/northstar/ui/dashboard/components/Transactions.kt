package com.example.northstar.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.dashboard.TransactionItem
import com.example.northstar.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight

@Composable
fun TransactionsList(transactions: List<TransactionItem>, onSeeAll: () -> Unit) {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Recent Transactions", fontSize = 15.sp, color = TextPrimary, fontFamily = InterFontFamily)
            Text("See All", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.clickable(onClick = onSeeAll), fontFamily = InterFontFamily)
        }

        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).clip(RoundedCornerShape(16.dp)).border(1.dp, ListDivider, RoundedCornerShape(16.dp)).background(White)) {
            Column {
                transactions.forEachIndexed { index, t ->
                    TransactionRow(t)
                    if (index < transactions.size - 1) HorizontalDivider(color = ListDivider, thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun TransactionRow(t: TransactionItem) {
    val key = (t.title + " " + t.category).lowercase(Locale.US)
    val icon = transactionIcon(t, key)
    val iconTint = transactionIconTint(t, key)

    Row(modifier = Modifier.fillMaxWidth().background(White).padding(horizontal = 14.dp, vertical = 13.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(38.dp).background(Color(0xFFFEF2F2), RoundedCornerShape(12.dp)).border(1.dp, Border, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = iconTint)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(t.category.ifBlank { "Transaction" }, fontSize = 13.sp, fontWeight = FontWeight.W600, color = TextPrimary, fontFamily = InterFontFamily)
            Text(SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(t.date)), fontSize = 10.sp, color = TextMuted, fontFamily = InterFontFamily, modifier = Modifier.padding(top = 2.dp))
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(String.format(Locale.US, "LKR %.2f", t.amount / 100.0), fontSize = 13.sp, fontWeight = FontWeight.W700, color = if (t.isIncome) Credit else Debit, fontFamily = InterFontFamily)
            Text(SimpleDateFormat("hh:mm a", Locale.US).format(Date(t.date)), fontSize = 10.sp, color = TextHint, fontFamily = InterFontFamily, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

private fun transactionIcon(t: TransactionItem, key: String) = when {
    t.isIncome -> Icons.Default.KeyboardArrowUp
    key.contains("rent") || key.contains("home") || key.contains("mortgage") -> Icons.Default.Home
    key.contains("food") || key.contains("meal") || key.contains("restaurant") -> Icons.Default.Restaurant
    key.contains("car") || key.contains("fuel") || key.contains("transport") || key.contains("taxi") || key.contains("ride") -> Icons.Default.DirectionsCar
    key.contains("shop") || key.contains("grocery") || key.contains("market") || key.contains("purchase") -> Icons.Default.ShoppingBag
    else -> Icons.Default.KeyboardArrowDown
}

private fun transactionIconTint(t: TransactionItem, key: String): Color = when {
    t.isIncome -> Credit
    key.contains("rent") || key.contains("home") || key.contains("mortgage") -> Debit
    key.contains("food") || key.contains("meal") || key.contains("restaurant") -> Color(0xFFE67700)
    key.contains("car") || key.contains("fuel") || key.contains("transport") || key.contains("taxi") || key.contains("ride") -> Color(0xFF0B7285)
    key.contains("shop") || key.contains("grocery") || key.contains("market") || key.contains("purchase") -> Color(0xFF5F3DC4)
    else -> Debit
}

