package com.example.northstar.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.northstar.Screen
import com.example.northstar.ui.auth.NorthStarInputField
import com.example.northstar.ui.lock.PinLockManager
import com.example.northstar.ui.lock.PinMode
import com.example.northstar.ui.lock.PinScreen
import com.example.northstar.ui.theme.*

@Composable
fun ProfileScreen(
    navController: NavController,
    pinLockManager: PinLockManager,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditName by remember { mutableStateOf(false) }
    var showEditEmail by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showPinSetup by remember { mutableStateOf(false) }

    // PIN setup overlay
    if (showPinSetup) {
        PinScreen(
            mode = PinMode.SETUP,
            onSuccess = { showPinSetup = false },
            pinLockManager = pinLockManager
        )
        return
    }

    // Edit name dialog
    if (showEditName) {
        EditFieldDialog(
            title = "Update Name",
            fieldLabel = "Display name",
            currentValue = uiState.displayName,
            onDismiss = {
                showEditName = false
                viewModel.clearMessages()
            },
            onConfirm = { newValue, _ ->
                viewModel.updateDisplayName(newValue)
            },
            isLoading = uiState.isLoading,
            successMessage = uiState.successMessage,
            errorMessage = uiState.errorMessage,
            onSuccess = {
                showEditName = false
                viewModel.clearMessages()
            }
        )
    }

    // Edit email dialog
    if (showEditEmail) {
        EditFieldDialog(
            title = "Update Email",
            fieldLabel = "New email address",
            currentValue = uiState.email,
            requiresPassword = true,
            onDismiss = {
                showEditEmail = false
                viewModel.clearMessages()
            },
            onConfirm = { newEmail, password ->
                viewModel.updateEmail(newEmail, password)
            },
            isLoading = uiState.isLoading,
            successMessage = uiState.successMessage,
            errorMessage = uiState.errorMessage,
            onSuccess = {
                showEditEmail = false
                viewModel.clearMessages()
            }
        )
    }

    // Sign out confirm dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            containerColor = White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "Sign Out",
                    fontWeight = FontWeight.W700,
                    fontSize = 17.sp,
                    color = TextPrimary,
                    fontFamily = InterFontFamily
                )
            },
            text = {
                Text(
                    "Are you sure you want to sign out of NorthStar?",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    fontFamily = InterFontFamily,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.signOut()
                        pinLockManager.clearPin()
                        showSignOutDialog = false
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Debit),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sign Out", fontWeight = FontWeight.W600, fontFamily = InterFontFamily)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showSignOutDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel", fontFamily = InterFontFamily)
                }
            }
        )
    }

    Scaffold(containerColor = Color.Transparent) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Surface)
                .padding(top = padding.calculateTopPadding())
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {

            // ── Dark header ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(Navy900)
                    .padding(horizontal = 20.dp)
                    .padding(top = 52.dp, bottom = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar circle
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Navy800)
                            .border(2.dp, White.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            uiState.displayName
                                .split(" ")
                                .filter { it.isNotBlank() }
                                .take(2)
                                .joinToString("") { it.first().uppercaseChar().toString() }
                                .ifBlank { "?" },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.W800,
                            color = White,
                            fontFamily = InterFontFamily
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        uiState.displayName.ifBlank { "User" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W800,
                        color = White,
                        letterSpacing = (-0.5).sp,
                        fontFamily = InterFontFamily
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        uiState.email,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W400,
                        color = White.copy(alpha = 0.45f),
                        fontFamily = InterFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Account section ──
            ProfileSectionHeader("Account")

            ProfileCard {
                ProfileRow(
                    icon = Icons.Outlined.Person,
                    label = "Display Name",
                    value = uiState.displayName.ifBlank { "Not set" },
                    onClick = { showEditName = true }
                )
                ProfileDivider()
                ProfileRow(
                    icon = Icons.Outlined.Email,
                    label = "Email Address",
                    value = uiState.email,
                    onClick = { showEditEmail = true }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Preferences section ──
            ProfileSectionHeader("Preferences")

            ProfileCard {
                ProfileRow(
                    icon = Icons.Outlined.CurrencyExchange,
                    label = "Default Currency",
                    value = "LKR",
                    onClick = null // read-only
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Security section ──
            ProfileSectionHeader("Security")

            ProfileCard {
                ProfileRow(
                    icon = Icons.Outlined.Lock,
                    label = if (pinLockManager.hasPin()) "Change PIN" else "Set App PIN",
                    value = if (pinLockManager.hasPin()) "Enabled" else "Not set",
                    valueColor = if (pinLockManager.hasPin()) Credit else TextMuted,
                    onClick = { showPinSetup = true }
                )
                if (pinLockManager.hasPin()) {
                    ProfileDivider()
                    ProfileRow(
                        icon = Icons.Outlined.LockOpen,
                        label = "Remove PIN",
                        value = "",
                        valueColor = Debit,
                        labelColor = Debit,
                        onClick = { pinLockManager.clearPin() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Sign out ──
            ProfileCard {
                ProfileRow(
                    icon = Icons.Outlined.Logout,
                    label = "Sign Out",
                    value = "",
                    labelColor = Debit,
                    iconTint = Debit,
                    onClick = { showSignOutDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // App version
            Text(
                "NorthStar · Version 1.0",
                fontSize = 11.sp,
                color = TextHint,
                fontFamily = InterFontFamily,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// ── Section header ──
@Composable
private fun ProfileSectionHeader(title: String) {
    Text(
        title,
        fontSize = 12.sp,
        fontWeight = FontWeight.W600,
        color = TextMuted,
        letterSpacing = 0.5.sp,
        fontFamily = InterFontFamily,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

// ── Card wrapper ──
@Composable
private fun ProfileCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(White, RoundedCornerShape(20.dp))
            .border(1.dp, Border, RoundedCornerShape(20.dp)),
        content = content
    )
}

// ── Divider ──
@Composable
private fun ProfileDivider() {
    HorizontalDivider(
        color = Separator,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

// ── Row ──
@Composable
private fun ProfileRow(
    icon: ImageVector,
    label: String,
    value: String,
    labelColor: Color = TextPrimary,
    valueColor: Color = TextSecondary,
    iconTint: Color = Navy900,
    onClick: (() -> Unit)?
) {
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
                .background(iconTint.copy(alpha = 0.08f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(17.dp),
                tint = iconTint
            )
        }

        Text(
            label,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            color = labelColor,
            fontFamily = InterFontFamily,
            modifier = Modifier.weight(1f)
        )

        if (value.isNotBlank()) {
            Text(
                value,
                fontSize = 13.sp,
                fontWeight = FontWeight.W500,
                color = valueColor,
                fontFamily = InterFontFamily
            )
        }

        if (onClick != null) {
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = TextHint
            )
        }
    }
}

// ── Edit field dialog ──
@Composable
private fun EditFieldDialog(
    title: String,
    fieldLabel: String,
    currentValue: String,
    requiresPassword: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    isLoading: Boolean,
    successMessage: String,
    errorMessage: String,
    onSuccess: () -> Unit
) {
    var fieldValue by remember { mutableStateOf(currentValue) }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(successMessage) {
        if (successMessage.isNotBlank()) onSuccess()
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Text(
                title,
                fontSize = 17.sp,
                fontWeight = FontWeight.W700,
                color = TextPrimary,
                fontFamily = InterFontFamily,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            NorthStarInputField(
                value = fieldValue,
                onValueChange = { fieldValue = it },
                label = fieldLabel,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (requiresPassword) KeyboardType.Email else KeyboardType.Text,
                    imeAction = if (requiresPassword) ImeAction.Next else ImeAction.Done
                )
            )

            if (requiresPassword) {
                Spacer(modifier = Modifier.height(12.dp))
                NorthStarInputField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Current password",
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Outlined.VisibilityOff
                                else Icons.Outlined.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = TextMuted
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )
            }

            if (errorMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    errorMessage,
                    fontSize = 12.sp,
                    color = Debit,
                    fontFamily = InterFontFamily
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel", fontFamily = InterFontFamily)
                }
                Button(
                    onClick = { onConfirm(fieldValue, password) },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save", fontFamily = InterFontFamily, fontWeight = FontWeight.W600)
                    }
                }
            }
        }
    }
}