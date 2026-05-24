package com.example.northstar.ui.budget.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.northstar.domain.model.Budget
import java.text.SimpleDateFormat
import java.util.*

data class AppCategoryItem(val name: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetFormModal(
    existingBudget: Budget? = null,
    onDismiss: () -> Unit,
    onConfirm: (category: String, limit: Long, period: String, threshold: Int, startDate: Long?, endDate: Long?) -> Unit
) {
    val categoriesList = remember {
        listOf(
            AppCategoryItem("Rent", Icons.Outlined.Home),
            AppCategoryItem("Food & Dining", Icons.Outlined.Restaurant),
            AppCategoryItem("Transport", Icons.Outlined.DirectionsCar),
            AppCategoryItem("Subscription", Icons.Outlined.CardMembership),
            AppCategoryItem("Utilities", Icons.Outlined.Lightbulb),
            AppCategoryItem("Entertainment", Icons.Outlined.ConfirmationNumber),
            AppCategoryItem("Health & Fitness", Icons.Outlined.FitnessCenter),
            AppCategoryItem("Shopping", Icons.Outlined.ShoppingBag)
        )
    }

    var selectedCategoryItem by remember {
        mutableStateOf(categoriesList.find { it.name.equals(existingBudget?.category, ignoreCase = true) } ?: categoriesList.first())
    }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var limitAmountStr by remember { mutableStateOf(existingBudget?.limitAmount?.toString() ?: "") }
    var warningThresholdStr by remember { mutableStateOf(existingBudget?.warningThreshold?.toString() ?: "80") }

    var startDate by remember { mutableStateOf(existingBudget?.startDate) }
    var endDate by remember { mutableStateOf(existingBudget?.endDate) }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    var limitHasError by remember { mutableStateOf(false) }
    var thresholdHasError by remember { mutableStateOf(false) }
    var dateRangeError by remember { mutableStateOf(false) }

    // State for date picker dialogs
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = startDate ?: System.currentTimeMillis()
    )
    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = endDate ?: System.currentTimeMillis()
    )

    // Use a bottom sheet for the modal to avoid nested dialog issues
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = if (existingBudget == null) "Create Budget" else "Edit Budget",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Category dropdown
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Category *", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedCard(
                        onClick = { if (existingBudget == null) dropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(selectedCategoryItem.icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                                Text(selectedCategoryItem.name, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                            if (existingBudget == null) Icon(Icons.Default.ArrowDropDown, null)
                        }
                    }
                    DropdownMenu(expanded = dropdownExpanded, onDismissRequest = { dropdownExpanded = false }) {
                        categoriesList.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Icon(cat.icon, null, modifier = Modifier.size(20.dp))
                                        Text(cat.name)
                                    }
                                },
                                onClick = { selectedCategoryItem = cat; dropdownExpanded = false }
                            )
                        }
                    }
                }
            }

            // Budget Limit (updated to handle decimals)
            Column {
                Text("Budget Limit *", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = limitAmountStr,
                    onValueChange = { limitAmountStr = it; limitHasError = false },
                    leadingIcon = { Text("LKR", fontWeight = FontWeight.Bold) },
                    isError = limitHasError,
                    supportingText = {
                        if (limitHasError) Text("Positive number required (decimals will be rounded)")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),  // Allows decimal point
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Start Date
            Column {
                Text("Start Date *", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showStartDatePicker = true }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = startDate?.let { dateFormat.format(Date(it)) } ?: "Select start date",
                            color = if (startDate == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                }
            }

            // End Date
            Column {
                Text("End Date *", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEndDatePicker = true }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = endDate?.let { dateFormat.format(Date(it)) } ?: "Select end date",
                            color = if (endDate == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                }
                if (dateRangeError) {
                    Text("End date must be after start date", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                }
            }

            // Warning Threshold
            Column {
                Text("Warning Threshold (%) *", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = warningThresholdStr,
                    onValueChange = { warningThresholdStr = it; thresholdHasError = false },
                    trailingIcon = { Text("%") },
                    isError = thresholdHasError,
                    supportingText = { if (thresholdHasError) Text("1-100") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Button(
                    onClick = {
                        // Parse limit as Double to support decimals, then round to nearest whole LKR
                        val limit = limitAmountStr.toDoubleOrNull()?.let {
                            kotlin.math.round(it).toLong()
                        }
                        val threshold = warningThresholdStr.toIntOrNull()
                        limitHasError = limit == null || limit <= 0L
                        thresholdHasError = threshold == null || threshold !in 1..100
                        val start = startDate
                        val end = endDate
                        dateRangeError = start == null || end == null || start >= end
                        if (!limitHasError && !thresholdHasError && !dateRangeError) {
                            onConfirm(
                                selectedCategoryItem.name,
                                limit!!,
                                "CUSTOM",
                                threshold!!,
                                start,
                                end
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text("Save Budget")
                }
            }
        }
    }

    // DatePicker dialogs (unchanged)
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        startDate = startDatePickerState.selectedDateMillis
                        showStartDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        endDate = endDatePickerState.selectedDateMillis
                        showEndDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }
}