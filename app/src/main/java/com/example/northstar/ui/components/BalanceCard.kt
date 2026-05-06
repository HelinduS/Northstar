package com.example.northstar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.theme.NeutralWhite
import com.example.northstar.ui.theme.PrimaryBlue

@Composable
fun BalanceCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(
                "Track Your Financial Goals",
                color = NeutralWhite.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "$ 243,320.00",
                color = NeutralWhite,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(18.dp))
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween
            ) {
                Text(
                    "Transfer Limit",
                    color = NeutralWhite.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Text(
                    "\$8,920.00",
                    color = NeutralWhite,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { 0.35f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = NeutralWhite,
                trackColor = NeutralWhite.copy(alpha = 0.3f)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Spent \$120.00",
                color = NeutralWhite.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }
    }
}