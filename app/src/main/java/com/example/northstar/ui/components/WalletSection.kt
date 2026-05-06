package com.example.northstar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.northstar.Screen
import com.example.northstar.ui.theme.NeutralCharcoal
import com.example.northstar.ui.theme.NeutralLightGrey
import com.example.northstar.ui.theme.NeutralWhite
import com.example.northstar.ui.theme.PrimaryBlue
import com.example.northstar.ui.theme.SecondaryAccentGreen

@Composable
fun WalletSection(navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Text(
                "Wallet",
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
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WalletCard(
                title = "Income",
                amount = "\$600",
                iconBg = PrimaryBlue,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(Screen.AddIncome.route) }
            )
            WalletCard(
                title = "Spending",
                amount = "\$600",
                iconBg = SecondaryAccentGreen,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(Screen.AddExpense.route) }
            )
        }
    }
}

@Composable
fun WalletCard(
    title: String,
    amount: String,
    iconBg: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralLightGrey)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        tint = NeutralWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = NeutralCharcoal.copy(alpha = 0.5f)
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                title,
                color = NeutralCharcoal.copy(alpha = 0.5f),
                fontSize = 13.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                amount,
                color = NeutralCharcoal,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}