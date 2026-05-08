package com.example.northstar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.theme.NeutralCharcoal
import com.example.northstar.ui.theme.NeutralLightGrey

@Composable
fun DashboardHeader(
    displayName: String = "",
    email: String = "",
    onSettingsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var showAvatarMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(NeutralLightGrey)
                    .clickable { showAvatarMenu = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = NeutralCharcoal,
                    modifier = Modifier.size(30.dp)
                )
            }
            DropdownMenu(
                expanded = showAvatarMenu,
                onDismissRequest = { showAvatarMenu = false }
            ) {
                Text(
                    text = "Account",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeutralCharcoal.copy(alpha = 0.55f)
                )
                DropdownMenuItem(
                    text = { Text("Profile") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = NeutralCharcoal
                        )
                    },
                    onClick = {
                        showAvatarMenu = false
                        onProfileClick()
                    }
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Logout") },
                    leadingIcon = {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F)
                        )
                    },
                    onClick = {
                        showAvatarMenu = false
                        onLogoutClick()
                    }
                )
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    text = if (displayName.isNotBlank()) "Hi, $displayName!" else "Hi there!",
                    color = NeutralCharcoal,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = email.ifBlank { "" },
                    color = NeutralCharcoal.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
        }
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(NeutralLightGrey)
                .clickable { onSettingsClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Settings",
                tint = NeutralCharcoal,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}