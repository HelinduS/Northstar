package com.example.northstar.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            Button(
                onClick = {
                    val targetAmount = (amount.toLongOrNull() ?: 0L) * 100L
                    val targetDate   = selectedDateMillis ?: 0L
                    if (name.isNotBlank() && targetAmount > 0L) {
                        onConfirm(name, targetAmount, targetDate)
                    }
                },
                enabled = name.isNotBlank() && amount.isNotBlank() && selectedDateMillis != null,
                shape  = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenDeep)
            ) { Text("Add Goal") }
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
            Button(
                onClick = {
                    // Validate name
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    // Validate amount
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
                shape  = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenDeep)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = cs.error)
            }
        }
    )
}