package com.example.northstar.ui.dashboard.components

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
import com.example.northstar.ui.theme.NeutralCharcoal
import com.example.northstar.ui.theme.NeutralLightGrey
import com.example.northstar.ui.theme.PrimaryBlue
import com.example.northstar.ui.theme.SemanticRed
import com.example.northstar.ui.theme.SecondaryAccentGreen

data class Tx(
    val name: String,
    val date: String,
    val amount: String,
    val isIncome: Boolean
)

@Composable
fun TransactionSection(navController: NavController) {
    val transactions = listOf(
        Tx("Freepik Premium", "Today, 2:00pm", "-\$15.00",  false),
        Tx("Salary Deposit",  "Yesterday",     "+\$600.00", true),
        Tx("Grocery Store",   "Mon, 3 May",    "-\$48.50",  false),
        Tx("Netflix",         "Sun, 2 May",    "-\$12.99",  false)
    )

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Text(
                "Latest Transaction",
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
        transactions.forEach { tx ->
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
                            tx.name,
                            color = NeutralCharcoal,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            tx.date,
                            color = NeutralCharcoal.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        tx.amount,
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