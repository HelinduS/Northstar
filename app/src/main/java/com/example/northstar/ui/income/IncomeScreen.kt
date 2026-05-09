package com.example.northstar.ui.income

import android.app.DatePickerDialog
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
import androidx.navigation.NavController
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

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    LaunchedEffect(uiState) {
        if (uiState is IncomeUiState.Success) {
            navController.popBackStack()
            viewModel.resetState()
        }
    }

    Scaffold(
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = {
                        if (amount.isNotEmpty()) {
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
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A56B4)),
                    enabled = uiState !is IncomeUiState.Loading && amount.isNotEmpty() && selectedSource.isNotEmpty() && selectedDate != 0L
                ) {
                    if (uiState is IncomeUiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Save Income", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).background(Color(0xFFF8F9FB))
        ) {
            // Header
            Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF1A56B4)).padding(bottom = 40.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp, start = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                        Text("Add Income", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("How much did you earn?", color = Color.White.copy(alpha = 0.8f))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                        Text(selectedCurrency, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = if (amount.isEmpty()) "0" else amount, color = Color.White, fontSize = 44.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Column(
                modifier = Modifier.offset(y = (-24).dp).padding(horizontal = 16.dp).background(Color.White, RoundedCornerShape(16.dp)).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Income Details", fontWeight = FontWeight.Bold, color = Color(0xFF1A56B4))

                // 1. Source Selection
                Box {
                    DetailDropdown(
                        label = "Income Source",
                        selected = if (selectedSource.isEmpty()) "Select Source" else selectedSource,
                        icon = Icons.Default.List,
                        onClick = { showSourceMenu = true }
                    )
                    DropdownMenu(expanded = showSourceMenu, onDismissRequest = { showSourceMenu = false }, modifier = Modifier.fillMaxWidth(0.85f)) {
                        val sources = listOf("Salary", "Freelance", "Social Media", "Google AdSense", "Investments", "E-commerce", "Affiliate", "Crypto", "Digital Products", "Tutoring", "Other")
                        sources.forEach { title ->
                            DropdownMenuItem(
                                text = { Text(title) },
                                leadingIcon = { Icon(Icons.Default.Add, null, tint = Color(0xFF1A56B4)) },
                                onClick = { viewModel.onSourceSelected(title); showSourceMenu = false }
                            )
                        }
                    }
                }

                if (selectedSource == "Freelance") {
                    OutlinedTextField(value = projectName, onValueChange = { projectName = it }, label = { Text("Project Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                }

                // 2. Currency Selection
                Box {
                    DetailDropdown(label = "Currency", selected = "$selectedCurrency (${getCurrencyName(selectedCurrency)})", icon = Icons.Default.Info, onClick = { showCurrencyMenu = true })
                    DropdownMenu(expanded = showCurrencyMenu, onDismissRequest = { showCurrencyMenu = false }, modifier = Modifier.fillMaxWidth(0.8f)) {
                        availableCurrencies.forEach { currency ->
                            DropdownMenuItem(
                                text = {
                                    Row {
                                        Text(currency, fontWeight = FontWeight.Bold, modifier = Modifier.width(50.dp))
                                        Text(getCurrencyName(currency), color = Color.Gray, fontSize = 14.sp)
                                    }
                                },
                                onClick = { viewModel.onCurrencySelected(currency); showCurrencyMenu = false }
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
                    label = { Text("Amount") },
                    prefix = { Text("$selectedCurrency ", color = Color(0xFF1A56B4), fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp)
                )

                // 4. Estimated Total Card
                if (selectedCurrency != "LKR" && amount.isNotEmpty()) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF)), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Estimated Total (LKR)", fontSize = 12.sp, color = Color.Gray)
                            Text("Rs. ${String.format(Locale.US, "%,.2f", totalLkrEstimate)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A56B4))
                        }
                    }
                }

                // 5. Exchange Rate Bar (Moved here)
                if (selectedCurrency != "LKR") {
                    Column {
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
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    } else {
                                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Rate")
                                    }
                                }
                            }
                        )
                        Text(
                            text = "Edit if bank rate differs.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }

                // 6. Date Selection
                DetailDropdown(
                    label = "Date Received",
                    selected = if (selectedDate == 0L) "Select Date" else dateFormatter.format(Date(selectedDate)),
                    icon = Icons.Default.DateRange,
                    onClick = {
                        val cal = Calendar.getInstance()
                        DatePickerDialog(context, { _, y, m, d -> cal.set(y, m, d); selectedDate = cal.timeInMillis }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                    }
                )

                // 7. Notes
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Add a note...") }, leadingIcon = { Icon(Icons.Default.Edit, null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            }
        }
    }
}

// Helpers
fun getCurrencyName(code: String): String = when(code) {
    "LKR" -> "Sri Lankan Rupee"
    "USD" -> "US Dollar"
    "EUR" -> "Euro"
    "GBP" -> "British Pound"
    "JPY" -> "Japanese Yen"
    "CHF" -> "Swiss Franc"
    "CAD" -> "Canadian Dollar"
    "AUD" -> "Australian Dollar"
    "INR" -> "Indian Rupee"
    "CNY" -> "Chinese Yuan"
    "USDT" -> "Tether"
    "BTC" -> "Bitcoin"
    "ETH" -> "Ethereum"
    "ALT" -> "Altcoins"
    else -> "Currency"
}

@Composable
fun DetailDropdown(label: String, selected: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        Row(
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color(0xFF1A56B4), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = selected, fontSize = 16.sp, color = if (selected.contains("Select")) Color.LightGray else Color.Black)
            }
            Icon(Icons.Default.ArrowDropDown, null, tint = Color.Gray)
        }
    }
}