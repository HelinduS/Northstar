package com.example.northstar.ui.budget.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun BudgetDeleteConfirmation(
    categoryName: String,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete tracking record budget?") },
        text = {
            Text("Are you sure you want to delete the '$categoryName' budget? This action removes your tracking boundary, but your underlying expense transaction files will remain safe and unaffected.")
        },
        confirmButton = {
            Button(onClick = onConfirmDelete, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Confirm Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}