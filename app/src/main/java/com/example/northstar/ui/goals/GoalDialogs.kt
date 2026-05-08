package com.example.northstar.ui.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.ui.theme.PrimaryBlue
import com.example.northstar.ui.theme.SemanticRed

@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Long, Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    val parsedAmount = amount.toLongOrNull() ?: 0L

    val isFormValid = name.trim().isNotEmpty() && parsedAmount > 0L

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "New Savings Goal",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
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
                        focusedBorderColor = PrimaryBlue,
                        focusedLabelColor = PrimaryBlue,
                        cursorColor = PrimaryBlue
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
                        focusedBorderColor = PrimaryBlue,
                        focusedLabelColor = PrimaryBlue,
                        cursorColor = PrimaryBlue
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Re-validate before submission
                    val finalParsedAmount = amount.toLongOrNull() ?: 0L
                    if (name.trim().isNotEmpty() && finalParsedAmount > 0L) {
                        val targetAmount = finalParsedAmount * 100L
                        onConfirm(name.trim(), targetAmount, 0L)
                    }
                },
                enabled = isFormValid,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Add Goal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SemanticRed)
            }
        }
    )
}

@Composable
fun ContributeDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    var amount by remember { mutableStateOf("") }

    val parsedAmount = amount.toLongOrNull() ?: 0L

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Add Savings",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount (LKR)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    focusedLabelColor = PrimaryBlue,
                    cursorColor = PrimaryBlue
                )
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val contribution = parsedAmount * 100L
                    onConfirm(contribution)
                },
                enabled = parsedAmount > 0,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Add Savings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SemanticRed)
            }
        }
    )
}