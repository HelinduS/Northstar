package com.example.northstar.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.northstar.Screen
import com.example.northstar.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val uiState by viewModel.uiState.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var notificationsEnabled by remember { mutableStateOf(true) }
    var reminderEnabled by remember { mutableStateOf(true) }
    var showClearDialog by remember { mutableStateOf(false) }

    // Clear data confirm dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = cs.surface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "Clear All Data",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = cs.onSurface
                )
            },
            text = {
                Text(
                    "This will permanently delete all your income, expense, and goal records. This action cannot be undone.",
                    fontSize = 14.sp,
                    color = cs.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Debit),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete All", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showClearDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    LaunchedEffect(uiState.exportSuccess) {
        if (uiState.exportSuccess) viewModel.resetState()
    }
    LaunchedEffect(uiState.clearSuccess) {
        if (uiState.clearSuccess) viewModel.resetState()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = (-0.3).sp,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenDeep
                )
            )
        },
        containerColor = cs.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Loading indicator
            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = cs.primary,
                    trackColor = cs.primary.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Error message
            if (uiState.error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Debit.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = uiState.error!!,
                        color = Debit,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Success message
            if (uiState.clearSuccess) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Credit.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "All data cleared successfully",
                        color = Credit,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Notifications ─────────────────────────────────────────────────
            SettingsSectionHeader(title = "Notifications")
            SettingsCard {
                SettingsToggleRow(
                    icon = Icons.Outlined.Notifications,
                    label = "Push Notifications",
                    description = "Receive app notifications",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
                SettingsDivider()
                SettingsToggleRow(
                    icon = Icons.Outlined.Alarm,
                    label = "Expense Reminders",
                    description = "Remind if no entry in 3 days",
                    checked = reminderEnabled,
                    onCheckedChange = { reminderEnabled = it }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Appearance ────────────────────────────────────────────────────
            SettingsSectionHeader(title = "Appearance")
            SettingsCard {
                SettingsToggleRow(
                    icon = Icons.Outlined.DarkMode,
                    label = "Dark Mode",
                    description = "Switch to dark theme",
                    checked = isDarkMode,
                    onCheckedChange = { viewModel.setDarkMode(it) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Data & Privacy ────────────────────────────────────────────────
            SettingsSectionHeader(title = "Data & Privacy")
            SettingsCard {
                SettingsNavigationRow(
                    icon = Icons.Outlined.Download,
                    label = "Export Data",
                    description = "Download your financial data",
                    onClick = { viewModel.exportData() }
                )
                SettingsDivider()
                SettingsNavigationRow(
                    icon = Icons.Outlined.DeleteForever,
                    label = "Clear All Data",
                    description = "Permanently delete all records",
                    labelColor = Debit,
                    iconTint = Debit,
                    onClick = { showClearDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── About ─────────────────────────────────────────────────────────
            SettingsSectionHeader(title = "About")
            SettingsCard {
                SettingsNavigationRow(
                    icon = Icons.Outlined.Info,
                    label = "App Version",
                    description = "1.0.0",
                    onClick = null
                )
                SettingsDivider()
                SettingsNavigationRow(
                    icon = Icons.Outlined.Policy,
                    label = "Privacy Policy",
                    description = "View our privacy policy",
                    onClick = { navController.navigate(Screen.PrivacyPolicy.route) }
                )
                SettingsDivider()
                SettingsNavigationRow(
                    icon = Icons.Outlined.Description,
                    label = "Terms of Service",
                    description = "View terms and conditions",
                    onClick = { navController.navigate(Screen.Terms.route) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "NorthStar · Personal Finance Management",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(cs.surface, RoundedCornerShape(20.dp))
            .border(1.dp, cs.outline, RoundedCornerShape(20.dp)),
        content = content
    )
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(cs.primary.copy(alpha = 0.10f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(17.dp),
                tint = cs.primary
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = cs.onSurface,
                fontFamily = InterFontFamily
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = cs.onSurfaceVariant,
                fontFamily = InterFontFamily
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                checkedTrackColor = cs.primary,
                uncheckedThumbColor = cs.onSurfaceVariant,
                uncheckedTrackColor = cs.surfaceVariant
            )
        )
    }
}

@Composable
private fun SettingsNavigationRow(
    icon: ImageVector,
    label: String,
    description: String,
    labelColor: Color? = null,
    iconTint: Color? = null,
    onClick: (() -> Unit)?
) {
    val cs = MaterialTheme.colorScheme
    val effectiveLabelColor = labelColor ?: cs.onSurface
    val effectiveIconTint = iconTint ?: cs.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(effectiveIconTint.copy(alpha = 0.10f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(17.dp),
                tint = effectiveIconTint
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = effectiveLabelColor,
                fontFamily = InterFontFamily
            )
            if (description.isNotEmpty()) {
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = cs.onSurfaceVariant,
                    fontFamily = InterFontFamily
                )
            }
        }

        if (onClick != null) {
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = cs.onSurfaceVariant
            )
        }
    }
}
