package com.example.northstar.ui.income

import android.app.DatePickerDialog
import android.view.ContextThemeWrapper
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.northstar.ui.notifications.NotificationViewModel
import com.example.northstar.ui.theme.GreenDeep
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeScreen(
    navController: NavController,
    viewModel: IncomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val cs = MaterialTheme.colorScheme

    val notificationViewModel: NotificationViewModel = viewModel(
        viewModelStoreOwner = context as ComponentActivity
    )

    val selectedSource by viewModel.selectedSource.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val availableCurrencies by viewModel.availableCurrencies.collectAsState()
    val currentRate by viewModel.exchangeRate.collectAsState()
    val isFetching by viewModel.isFetchingRate.collectAsState()
    val totalLkrEstimate by viewModel.totalLkrEstimate.collectAsState()

    var amount by remember { mutableStateOf("") }
    var projectName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedDate by remember { mutableLongStateOf(0L) }

    var showCurrencyMenu by remember { mutableStateOf(false) }
    var showSourceMenu by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    LaunchedEffect(uiState) {
        if (uiState is IncomeUiState.Success) {
            val amountDouble = amount.toDoubleOrNull() ?: 0.0
            val amountInLkr = if (selectedCurrency == "LKR") amountDouble else totalLkrEstimate
            notificationViewModel.notifyIncomeLogged(
                amount = amountInLkr,
                newBalance = amountInLkr
            )
            navController.popBackStack()
            viewModel.resetState()
        }
    }

    // Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    "Confirm Income",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailRow("Source", selectedSource.ifEmpty { "Not selected" })
                    if (selectedSource == "Freelance" && projectName.isNotBlank()) {
                        DetailRow("Project", projectName)
                    }
                    DetailRow(
                        "Amount",
                        "$selectedCurrency ${if (amount.isEmpty()) "0" else amount}"
                    )
                    if (selectedCurrency != "LKR" && amount.isNotEmpty()) {
                        DetailRow(
                            "≈ LKR",
                            "Rs. ${String.format(Locale.US, "%,.2f", totalLkrEstimate)}"
                        )
                    }
                    DetailRow(
                        "Date",
                        if (selectedDate == 0L) "Not selected"
                        else dateFormatter.format(Date(selectedDate))
                    )
                    if (notes.isNotBlank()) {
                        DetailRow("Note", notes)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.addIncome(
                            sourceType = selectedSource,
                            projectName = if (selectedSource == "Freelance") projectName else null,
                            amountStr = amount,
                            currency = selectedCurrency,
                            exchangeRate = currentRate,
                            date = selectedDate,
                            notes = notes.ifBlank { null }
                        )
                    }
                ) {
                    Text("OK", color = GreenDeep, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = cs.surface,
            titleContentColor = cs.onSurface,
            textContentColor = cs.onSurfaceVariant
        )
    }

    Scaffold(
        containerColor = cs.background,
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = cs.surface
            ) {
                Button(
                    onClick = {
                        if (amount.isNotEmpty()) {
                            showConfirmDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenDeep,
                        contentColor = Color.White
                    ),
                    enabled = uiState !is IncomeUiState.Loading &&
                            amount.isNotEmpty() &&
                            selectedSource.isNotEmpty() &&
                            selectedDate != 0L
                ) {
                    if (uiState is IncomeUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            "Save Income",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(cs.background)
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GreenDeep)
                    .padding(bottom = 40.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                null,
                                tint = Color.White
                            )
                        }
                        Text(
                            "Add Income",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "How much did you earn?",
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            selectedCurrency,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (amount.isEmpty()) "0" else amount,
                            color = Color.White,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            // Form Container
            Column(
                modifier = Modifier
                    .offset(y = (-24).dp)
                    .padding(horizontal = 16.dp)
                    .background(cs.surface, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Income Details",
                    fontWeight = FontWeight.Bold,
                    color = cs.onSurface
                )

                // 1. Source Selection
                Box {
                    DetailDropdown(
                        label = "Income Source",
                        selected = if (selectedSource.isEmpty()) "Select Source" else selectedSource,
                        icon = Icons.Default.List,
                        onClick = { showSourceMenu = true }
                    )
                    DropdownMenu(
                        expanded = showSourceMenu,
                        onDismissRequest = { showSourceMenu = false },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(cs.surface)
                    ) {
                        val sources = listOf(
                            "Salary",
                            "Freelance",
                            "Social Media",
                            "Google AdSense",
                            "Investments",
                            "E-commerce",
                            "Affiliate",
                            "Crypto",
                            "Digital Products",
                            "Tutoring",
                            "Other"
                        )
                        sources.forEach { title ->
                            DropdownMenuItem(
                                text = { Text(title, color = cs.onSurface) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Add,
                                        null,
                                        tint = cs.onSurfaceVariant
                                    )
                                },
                                onClick = {
                                    viewModel.onSourceSelected(title)
                                    showSourceMenu = false
                                }
                            )
                        }
                    }
                }

                if (selectedSource == "Freelance") {
                    OutlinedTextField(

                        value = projectName,
                        onValueChange = { projectName = it },
                        label = { Text("Project Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // 2. Currency Selection
                Box {
                    DetailDropdown(
                        label = "Currency",

                        selected = "$selectedCurrency (${getCurrencyName(selectedCurrency)})",

                        icon = Icons.Default.Info,

                        onClick = { showCurrencyMenu = true }
                    )

                    DropdownMenu(
                        expanded = showCurrencyMenu,
                        onDismissRequest = { showCurrencyMenu = false },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .background(cs.surface)
                    ) {
                        availableCurrencies.forEach { currency ->
                            DropdownMenuItem(
                                text = {
                                    Row {
                                        Text(
                                            currency,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.width(50.dp),
                                            color = cs.onSurface
                                        )
                                        Text(
                                            getCurrencyName(currency),
                                            color = cs.onSurfaceVariant,
                                            fontSize = 14.sp
                                        )
                                    }
                                },
                                onClick = {
                                    viewModel.onCurrencySelected(currency)
                                    showCurrencyMenu = false
                                }
                            )
                        }
                    }
                }

                // 3. Amount Field
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() || c == '.' }) {
                            amount = it
                            viewModel.updateLiveAmount(it)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Amount", color = cs.onSurfaceVariant) },
                    prefix = {
                        Text(
                            "$selectedCurrency ",
                            color = cs.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cs.primary,

                        unfocusedBorderColor = cs.outline,

                        cursorColor = cs.primary
                    )
                )

                // 4. Estimated Total Card
                if (selectedCurrency != "LKR" && amount.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E9)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFC8E6C9), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Estimated Total (LKR)",

                                fontSize = 13.sp,

                                color = Color.Gray,

                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Rs. ${String.format(Locale.US, "%,.2f", totalLkrEstimate)}",

                                fontSize = 24.sp,

                                fontWeight = FontWeight.Bold,

                                color = Color(0xFF388E3C)
                            )
                        }
                    }
                }

                // 5. Exchange Rate Bar
                if (selectedCurrency != "LKR") {
                    OutlinedTextField(
                        value = currentRate.toString(),
                        onValueChange = { viewModel.updateExchangeRate(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Exchange Rate (1 $selectedCurrency to LKR)") },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        trailingIcon = {
                            IconButton(onClick = { viewModel.refreshRate() }) {
                                if (isFetching) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Default.Refresh, null)
                                }
                            }
                        }
                    )
                }

                // 6. Date Selection - FIXED: uses a light built-in theme
                DetailDropdown(
                    label = "Date Received",
                    selected = if (selectedDate == 0L) "Select Date"
                    else dateFormatter.format(Date(selectedDate)),
                    icon = Icons.Default.DateRange,
                    onClick = {
                        val cal = Calendar.getInstance()
                        val themedContext = ContextThemeWrapper(
                            context,
                            android.R.style.Theme_Material_Light_Dialog
                        )
                        DatePickerDialog(
                            themedContext,
                            { _, y, m, d ->
                                cal.set(y, m, d)
                                selectedDate = cal.timeInMillis
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                )

                // 7. Notes
                OutlinedTextField(
                    value = notes,

                    onValueChange = { notes = it },

                    label = { Text("Add a note...") },

                    leadingIcon = {
                        Icon(
                            Icons.Default.Edit,
                            null,
                            tint = cs.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DetailDropdown(
    label: String,
    selected: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = cs.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, cs.outline, RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    null,
                    tint = cs.onSurface,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = selected,
                    fontSize = 16.sp,
                    color = if (selected.contains("Select")) cs.onSurfaceVariant
                    else cs.onSurface
                )
            }
            Icon(
                Icons.Default.ArrowDropDown,
                null,
                tint = cs.onSurfaceVariant
            )
        }
    }
}

fun getCurrencyName(code: String): String = when (code) {
    "LKR"  -> "Sri Lankan Rupee"
    "USD"  -> "US Dollar"
    "EUR"  -> "Euro"
    "GBP"  -> "British Pound"
    "JPY"  -> "Japanese Yen"
    "CHF"  -> "Swiss Franc"
    "CAD"  -> "Canadian Dollar"
    "AUD"  -> "Australian Dollar"
    "INR"  -> "Indian Rupee"
    "CNY"  -> "Chinese Yuan"
    "USDT" -> "Tether"
    "BTC"  -> "Bitcoin"
    "ETH"  -> "Ethereum"
    "ALT"  -> "Altcoins"
    else   -> "Currency"
}