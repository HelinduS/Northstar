package com.example.northstar.ui.income

import android.app.DatePickerDialog
import androidx.compose.foundation.background
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

    var amount by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("LKR") }

    var selectedSource by remember { mutableStateOf("") }
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
                            val rate = if (selectedCurrency == "USD") 300.0 else 1.0
                            viewModel.addIncome(
                                selectedSource,
                                projectName.ifBlank { null },
                                amount,
                                selectedCurrency,
                                rate,
                                selectedDate,
                                notes.ifBlank { null }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                    shape = RoundedCornerShape(12.dp),

                    enabled = uiState !is IncomeUiState.Loading &&
                            amount.isNotEmpty() &&
                            selectedSource.isNotEmpty() &&
                            selectedDate != 0L
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF8F9FB))
        ) {
            // --- HEADER SECTION ---
            Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF1A56B4)).padding(bottom = 32.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp, start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                        }
                        Text("Add Income", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("How much did you earn?", color = Color.White.copy(alpha = 0.8f))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
                        Text(selectedCurrency, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(12.dp))

                        Text(if (amount.isEmpty()) "0.00" else amount, color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) amount = it },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 20.sp),
                        placeholder = { Text("Enter amount", color = Color.White.copy(alpha = 0.5f)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                            cursorColor = Color.White
                        )
                    )
                }
            }

            // --- DETAILS CARD ---
            Column(
                modifier = Modifier
                    .offset(y = (-20).dp)
                    .padding(horizontal = 16.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Income Details", fontWeight = FontWeight.Bold, color = Color(0xFF1A56B4))

                // --- CURRENCY DROPDOWN ---
                Box {
                    DetailDropdown(
                        label = "Currency",
                        selected = selectedCurrency,
                        icon = Icons.Default.Info,
                        onClick = { showCurrencyMenu = true }
                    )
                    DropdownMenu(
                        expanded = showCurrencyMenu,
                        onDismissRequest = { showCurrencyMenu = false },
                        modifier = Modifier.fillMaxWidth(0.8f).background(Color.White)
                    ) {
                        CurrencyMenuItem("LKR", "Sri Lankan Rupee", Icons.Default.Info) {
                            selectedCurrency = "LKR"; showCurrencyMenu = false
                        }
                        CurrencyMenuItem("USD", "United States Dollar", Icons.Default.Info) {
                            selectedCurrency = "USD"; showCurrencyMenu = false
                        }
                    }
                }

                // --- SOURCE DROPDOWN ---
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
                        modifier = Modifier.fillMaxWidth(0.8f).background(Color.White)
                    ) {
                        SourceMenuItem("SALARY", "Fixed recurring monthly pay", Icons.Default.Lock) {
                            selectedSource = "SALARY"; showSourceMenu = false
                        }
                        SourceMenuItem("FREELANCE", "Variable project-based income", Icons.Default.List) {
                            selectedSource = "FREELANCE"; showSourceMenu = false
                        }
                        SourceMenuItem("INVESTMENT", "Dividends and market returns", Icons.Default.Star) {
                            selectedSource = "INVESTMENT"; showSourceMenu = false
                        }
                    }
                }

                // --- DATE SELECTION ---
                DetailDropdown(
                    label = "Date Received",
                    selected = if (selectedDate == 0L) "Select Date" else dateFormatter.format(Date(selectedDate)),
                    icon = Icons.Default.DateRange,
                    onClick = {
                        val cal = Calendar.getInstance()
                        DatePickerDialog(context, { _, y, m, d ->
                            cal.set(y, m, d)
                            selectedDate = cal.timeInMillis
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                    }
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Add a note...") },
                    leadingIcon = { Icon(Icons.Default.Edit, null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@Composable
fun SourceMenuItem(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    DropdownMenuItem(
        text = {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Icon(icon, null, tint = Color(0xFF1A56B4), modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(subtitle, fontSize = 12.sp, color = Color.Gray)
                }
            }
        },
        onClick = onClick
    )
}

@Composable
fun CurrencyMenuItem(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    DropdownMenuItem(
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color(0xFF1A56B4), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(subtitle, fontSize = 11.sp, color = Color.Gray)
                }
            }
        },
        onClick = onClick
    )
}

@Composable
fun DetailDropdown(label: String, selected: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Column {
        Text(label, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
            color = Color.White
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color(0xFF1A56B4))
                Spacer(Modifier.width(12.dp))
                Text(selected, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.Gray)
            }
        }
    }
}