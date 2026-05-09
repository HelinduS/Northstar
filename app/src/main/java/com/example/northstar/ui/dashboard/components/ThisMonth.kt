package com.example.northstar.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.northstar.ui.theme.*
import java.util.*

@Composable
fun ThisMonthCard(income: Long, expenses: Long) {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("This Month", fontSize = 15.sp, fontWeight = FontWeight.W700, color = TextPrimary, fontFamily = InterFontFamily)
            Text(java.text.SimpleDateFormat("MMMM yyyy", Locale.US).format(Date()), fontSize = 11.sp, fontWeight = FontWeight.W600, color = TextSecondary, fontFamily = InterFontFamily)
        }

        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).background(White, RoundedCornerShape(20.dp)).border(1.dp, Border, RoundedCornerShape(20.dp)).padding(18.dp)) {
            Column {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("NET SAVED", fontSize = 10.sp, fontWeight = FontWeight.W600, color = TextMuted, letterSpacing = 0.5.sp, fontFamily = InterFontFamily)
                        val balance = (income - expenses) / 100.0
                        Text(String.format(Locale.US, "− LKR %.2f", balance), fontSize = 22.sp, fontWeight = FontWeight.W800, color = Debit, letterSpacing = (-0.8).sp, fontFamily = InterFontFamily)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("SPEND TYPE", fontSize = 10.sp, fontWeight = FontWeight.W600, color = TextMuted, letterSpacing = 0.5.sp, fontFamily = InterFontFamily)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 6.dp)) {
                            val fixedPercent = if (expenses > 0) ((expenses * 0.91) / expenses * 100).toInt() else 0
                            val freePercent = if (expenses > 0) ((expenses * 0.09) / expenses * 100).toInt() else 0
                            SpendChip(dot = Navy900, label = "${fixedPercent}% Fixed")
                            SpendChip(dot = TextMuted, label = "${freePercent}% Free")
                        }
                    }
                }

                HorizontalDivider(color = Separator, thickness = 1.dp, modifier = Modifier.padding(bottom = 14.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(modifier = Modifier.size(6.dp).background(Credit, RoundedCornerShape(99.dp)))
                        Text("Income", fontSize = 10.sp, fontWeight = FontWeight.W600, color = TextSecondary, fontFamily = InterFontFamily)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(modifier = Modifier.size(6.dp).background(Debit, RoundedCornerShape(99.dp)))
                        Text("Expenses", fontSize = 10.sp, fontWeight = FontWeight.W600, color = TextSecondary, fontFamily = InterFontFamily)
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(Separator, RoundedCornerShape(99.dp))) {
                    val total = income + expenses
                    val incomeRatio = if (total > 0) income.toFloat() / total else 0f
                    Row(modifier = Modifier.fillMaxWidth().height(8.dp)) {
                        if (incomeRatio > 0f) {
                            Box(modifier = Modifier.weight(incomeRatio).height(8.dp).background(Credit, RoundedCornerShape(99.dp)))
                        }
                        if (incomeRatio < 1f) {
                            Box(modifier = Modifier.weight(1f - incomeRatio).height(8.dp).background(Debit, RoundedCornerShape(99.dp)))
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(String.format(Locale.US, "LKR %.2f", income / 100.0), fontSize = 11.sp, fontWeight = FontWeight.W700, color = Credit, fontFamily = InterFontFamily)
                    Text(String.format(Locale.US, "LKR %.2f", expenses / 100.0), fontSize = 11.sp, fontWeight = FontWeight.W700, color = Debit, fontFamily = InterFontFamily)
                }
            }
        }
    }
}