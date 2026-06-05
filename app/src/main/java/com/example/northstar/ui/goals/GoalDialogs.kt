package com.example.northstar.ui.goals

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.theme.GreenDeep
import com.example.northstar.ui.theme.InterFontFamily
import com.example.northstar.domain.model.Goal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────────────────

@Composable
fun AddGoalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = "press_scale"
    )

    val greenText = Color(0xFF1D9E75)

    Row(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                onClick           = onClick
            )
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(greenText.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = greenText,
                modifier = Modifier.size(13.dp)
            )
        }
        Text(
            text       = "Add goal",
            fontSize   = 14.sp,
            fontWeight = FontWeight.Medium,
            color      = greenText,
            fontFamily = InterFontFamily
        )
    }
}

// ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Long, Long) -> Unit,
    prefillName: String? = null,
    prefillAmount: Double? = null
) {
    val cs = MaterialTheme.colorScheme

    var name   by remember { mutableStateOf(prefillName ?: "") }
    var amount by remember { mutableStateOf(prefillAmount?.toLong()?.toString() ?: "") }

    var showDatePicker     by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }

    val configuration = LocalConfiguration.current
    val dateFormatter = remember(configuration) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    }

    val displayDate = remember(selectedDateMillis) {
        selectedDateMillis?.let { dateFormatter.format(Date(it)) } ?: "Select target date"
    }

    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) {
                    Text("OK", color = cs.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = cs.error)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "New Savings Goal",
                fontSize = 18.sp,
                color = cs.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Goal Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cs.primary,
                        focusedLabelColor  = cs.primary
                    )
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Target Amount (LKR)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cs.primary,
                        focusedLabelColor  = cs.primary
                    )
                )
                OutlinedTextField(
                    value = displayDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Target Date") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = cs.primary)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cs.primary,
                        focusedLabelColor  = cs.primary
                    )
                )
            }
        },
        confirmButton = {
            val isEnabled = name.isNotBlank() && amount.isNotBlank() && selectedDateMillis != null
            Button(
                onClick = {
                    val targetAmount = (amount.toLongOrNull() ?: 0L) * 100L
                    val targetDate   = selectedDateMillis ?: 0L
                    if (name.isNotBlank() && targetAmount > 0L) {
                        onConfirm(name, targetAmount, targetDate)
                    }
                },
                enabled = isEnabled,
                shape   = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isEnabled)
                                Brush.linearGradient(listOf(Color(0xFF0F6E56), Color(0xFF1D9E75)))
                            else
                                Brush.linearGradient(listOf(Color(0xFFB0B0B0), Color(0xFFB0B0B0)))
                        )
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Flag,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                        Text(
                            "Add Goal",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color      = Color.White,
                            fontFamily = InterFontFamily
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = cs.error)
            }
        }
    )
}

@Composable
fun ContributeDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    var amount by remember { mutableStateOf("") }
    val parsedAmount = amount.toLongOrNull() ?: 0L

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = { Text("Add Savings", fontSize = 18.sp) },
        text = {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount (LKR)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = cs.primary,
                    focusedLabelColor  = cs.primary
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(parsedAmount * 100L) },
                enabled = parsedAmount > 0,
                shape  = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenDeep)
            ) { Text("Add Savings") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = cs.error)
            }
        }
    )
}

// ─────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGoalDialog(
    goal: Goal,
    onDismiss: () -> Unit,
    onConfirm: (name: String, targetAmount: Long, targetDate: Long) -> Unit
) {
    val cs = MaterialTheme.colorScheme

    var name           by remember { mutableStateOf(goal.name) }
    var amount         by remember { mutableStateOf((goal.targetAmount / 100L).toString()) }
    var nameError      by remember { mutableStateOf(false) }
    var amountError    by remember { mutableStateOf(false) }
    var amountErrorMsg by remember { mutableStateOf("") }

    var showDatePicker     by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(
        if (goal.targetDate > 0L) goal.targetDate else null
    )}

    val configuration = LocalConfiguration.current
    val dateFormatter = remember(configuration) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    }

    val displayDate = remember(selectedDateMillis) {
        selectedDateMillis?.let { dateFormatter.format(Date(it)) } ?: "Select target date"
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (goal.targetDate > 0L) goal.targetDate else null
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) {
                    Text("OK", color = cs.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = cs.error)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Edit Goal",
                fontSize = 18.sp,
                color = cs.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // ── Goal Name ──────────────────────────────────────────
                OutlinedTextField(
                    value         = name,
                    onValueChange = { name = it; nameError = false },
                    label         = { Text("Goal Name") },
                    isError       = nameError,
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cs.primary,
                        focusedLabelColor  = cs.primary
                    )
                )
                if (nameError) {
                    Text(
                        "Name cannot be empty",
                        color    = cs.error,
                        fontSize = 11.sp
                    )
                }

                // ── Target Amount ──────────────────────────────────────
                OutlinedTextField(
                    value         = amount,
                    onValueChange = { amount = it; amountError = false },
                    label         = { Text("Target Amount (LKR)") },
                    isError       = amountError,
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cs.primary,
                        focusedLabelColor  = cs.primary
                    )
                )
                if (amountError) {
                    Text(
                        amountErrorMsg,
                        color    = cs.error,
                        fontSize = 11.sp
                    )
                }

                // ── Target Date ────────────────────────────────────────
                OutlinedTextField(
                    value         = displayDate,
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Target Date") },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    trailingIcon  = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = cs.primary)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cs.primary,
                        focusedLabelColor  = cs.primary
                    )
                )

                // ── Info card: current saved amount ────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0FDF4))
                        .border(0.5.dp, Color(0xFFA3E9C9), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            "Already saved: LKR ${NumberFormat.getInstance().format(goal.savedAmount / 100L)}",
                            fontSize   = 12.sp,
                            color      = GreenDeep,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {
            val isEnabled = name.isNotBlank() && amount.isNotBlank()
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    val parsedAmount = amount.toLongOrNull()
                    when {
                        parsedAmount == null || parsedAmount <= 0L -> {
                            amountError    = true
                            amountErrorMsg = "Please enter a valid amount"
                            return@Button
                        }
                        parsedAmount * 100L < goal.savedAmount -> {
                            amountError    = true
                            amountErrorMsg = "Must be ≥ already saved (LKR ${goal.savedAmount / 100L})"
                            return@Button
                        }
                    }
                    onConfirm(
                        name.trim(),
                        parsedAmount!! * 100L,
                        selectedDateMillis ?: goal.targetDate
                    )
                },
                shape   = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF0F6E56), Color(0xFF1D9E75)))
                        )
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Flag,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                        Text(
                            "Save Changes",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color      = Color.White,
                            fontFamily = InterFontFamily
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = cs.error)
            }
        }
    )
}