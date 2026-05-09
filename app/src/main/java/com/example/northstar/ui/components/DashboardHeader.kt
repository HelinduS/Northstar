package com.example.northstar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.theme.NeutralWhite

@Composable
fun DashboardHeader(
    modifier: Modifier = Modifier,
    displayName: String = "",
    email: String = "",
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var showAvatarMenu by remember { mutableStateOf(false) }
    val avatarInitials = initials(displayName, email)
    val greetingName = displayName.ifBlank { email.substringBefore('@').ifBlank { "there" } }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(2.dp, CircleShape, clip = false)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(DashboardPrimary, DashboardPrimaryGradientEnd)
                        )
                    )
                    .clickable { showAvatarMenu = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarInitials,
                    color = NeutralWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
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
                    color = DashboardTextSecondary
                )
                DropdownMenuItem(
                    text = { Text("Profile") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = DashboardTextPrimary
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
                    text = "Good evening,",
                    color = DashboardTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = greetingName,
                    color = DashboardTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Box(
            modifier = Modifier
                .size(38.dp)
                .shadow(2.dp, RoundedCornerShape(12.dp), clip = false)
                .clip(RoundedCornerShape(12.dp))
                .background(NeutralWhite)
                .border(1.dp, DashboardBorder, RoundedCornerShape(12.dp))
                .clickable { onNotificationClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = DashboardPrimary,
                    modifier = Modifier.size(19.dp)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-8).dp)
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF03E3E))
                        .border(1.dp, NeutralWhite, CircleShape)
                )
            }
        }
    }
}