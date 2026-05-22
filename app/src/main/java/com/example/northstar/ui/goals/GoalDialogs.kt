package com.example.northstar.ui.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.theme.GreenDeep
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Long, Long) -> Unit,
    // Optional pre-fill from a tapped template
    prefillName: String? = null,
    prefillAmount: Double? = null
) {
    val cs = MaterialTheme.colorScheme

    // Initialise fields from template prefill (if any)
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
