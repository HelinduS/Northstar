package com.example.northstar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.northstar.Screen
import com.example.northstar.ui.dashboard.TransactionItem
import com.example.northstar.ui.theme.NeutralCharcoal
import com.example.northstar.ui.theme.NeutralLightGrey
import com.example.northstar.ui.theme.PrimaryBlue
import com.example.northstar.ui.theme.SemanticRed
import com.example.northstar.ui.theme.SecondaryAccentGreen

@Composable
fun TransactionSection(
    navController: NavController,
    transactions: List<TransactionItem> = emptyList()
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Text(
                "Latest Transactions",
                color = NeutralCharcoal,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "See All",
                color = PrimaryBlue,
                fontSize = 13.sp,
                modifier = Modifier.clickable {
                    navController.navigate(Screen.Analytics.route)
                }
            )
        }
        Spacer(Modifier.height(14.dp))

        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No transactions this month yet",
                    color = NeutralCharcoal.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )
            }
        } else {
            transactions.forEach { tx ->
                val amountLkr = tx.amount / 100.0
                val amountText = if (tx.isIncome)
                    "+ LKR ${String.format("%,.2f", amountLkr)}"
                else
                    "- LKR ${String.format("%,.2f", amountLkr)}"

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NeutralLightGrey)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (tx.isIncome) SecondaryAccentGreen.copy(alpha = 0.15f)
                                    else SemanticRed.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (tx.isIncome) Icons.Default.KeyboardArrowDown
                                else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (tx.isIncome) SecondaryAccentGreen else SemanticRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                tx.title,
                                color = NeutralCharcoal,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                tx.category,
                                color = NeutralCharcoal.copy(alpha = 0.5f),
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            amountText,
                            color = if (tx.isIncome) SecondaryAccentGreen else SemanticRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}