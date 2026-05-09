package com.example.northstar.ui.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.northstar.ui.theme.*

data class CategoryOption(val value: String, val label: String, val icon: ImageVector)
data class OptionItem(val value: String, val label: String, val icon: ImageVector)

val expenseCategories = listOf(
    CategoryOption("RENT", "Rent", Icons.Default.Home),
    CategoryOption("FOOD", "Food & Dining", Icons.Default.Star),
    CategoryOption("TRANSPORT", "Transport", Icons.Default.Search),
    CategoryOption("SUBSCRIPTIONS", "Subscriptions", Icons.Default.Refresh),
    CategoryOption("UTILITIES", "Utilities", Icons.Default.Build),
    CategoryOption("ENTERTAINMENT", "Entertainment", Icons.Default.PlayArrow),
    CategoryOption("HEALTH", "Health & Fitness", Icons.Default.Favorite),
    CategoryOption("SHOPPING", "Shopping", Icons.Default.ShoppingCart),
    CategoryOption("CRYPTO", "Crypto", Icons.Default.Star),
    CategoryOption("OTHER", "Other", Icons.Default.MoreVert)
)

val expenseTypes = listOf(
    OptionItem("COMMITTED", "Committed", Icons.Default.Lock),
    OptionItem("DISCRETIONARY", "Discretionary", Icons.Default.List)
)

val paymentMethods = listOf(
    OptionItem("CARD", "Card", Icons.Default.Edit),
    OptionItem("CASH", "Cash", Icons.Default.Star),
    OptionItem("BANK_TRANSFER", "Bank Transfer", Icons.Default.Send),
    OptionItem("OTHER", "Other", Icons.Default.MoreVert)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    navController: NavController,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedExpenseType by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var expenseTypeExpanded by remember { mutableStateOf(false) }
    var paymentMethodExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.resetSavedState()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Expense",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue
                )
            )
        },
        containerColor = NeutralLightGrey
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Amount Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(PrimaryBlue, Color(0xFF1A5CB8))
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "How much did you spend?",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "LKR",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(end = 8.dp, top = 8.dp)
                        )
                        Text(
                            text = if (amount.isEmpty()) "0.00" else amount,
                            color = Color.White,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        placeholder = {
                            Text(
                                "Enter amount",
                                color = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            color = Color.White
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White.copy(alpha = 0.8f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                            cursorColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Details Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = NeutralWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "Expense Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PrimaryBlue
                    )

                    // Category dropdown
                    Column {
                        Text(
                            text = "Category",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeutralCharcoal.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        ExposedDropdownMenuBox(
                            expanded = categoryExpanded,
                            onExpandedChange = { categoryExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = expenseCategories.find {
                                    it.value == selectedCategory
                                }?.label ?: "",
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text("Select category") },
                                leadingIcon = {
                                    Icon(
                                        expenseCategories.find {
                                            it.value == selectedCategory
                                        }?.icon ?: Icons.Default.List,
                                        contentDescription = null,
                                        tint = PrimaryBlue
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = categoryExpanded
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { categoryExpanded = false }
                            ) {
                                expenseCategories.forEach { category ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Icon(
                                                    category.icon,
                                                    contentDescription = null,
                                                    tint = PrimaryBlue,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Text(category.label)
                                            }
                                        },
                                        onClick = {
                                            selectedCategory = category.value
                                            categoryExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Expense Type dropdown
                    Column {
                        Text(
                            text = "Expense Type",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeutralCharcoal.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        ExposedDropdownMenuBox(
                            expanded = expenseTypeExpanded,
                            onExpandedChange = { expenseTypeExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = expenseTypes.find {
                                    it.value == selectedExpenseType
                                }?.label ?: "",
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text("Committed or Discretionary?") },
                                leadingIcon = {
                                    Icon(
                                        expenseTypes.find {
                                            it.value == selectedExpenseType
                                        }?.icon ?: Icons.Default.List,
                                        contentDescription = null,
                                        tint = PrimaryBlue
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expenseTypeExpanded
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expenseTypeExpanded,
                                onDismissRequest = { expenseTypeExpanded = false }
                            ) {
                                expenseTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Icon(
                                                    type.icon,
                                                    contentDescription = null,
                                                    tint = PrimaryBlue,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Column {
                                                    Text(
                                                        text = type.label,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                    Text(
                                                        text = if (type.value == "COMMITTED")
                                                            "Fixed recurring (rent, subscriptions)"
                                                        else
                                                            "Variable optional (dining, shopping)",
                                                        fontSize = 11.sp,
                                                        color = NeutralCharcoal.copy(alpha = 0.6f)
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            selectedExpenseType = type.value
                                            expenseTypeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Payment Method dropdown
                    Column {
                        Text(
                            text = "Payment Method",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeutralCharcoal.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        ExposedDropdownMenuBox(
                            expanded = paymentMethodExpanded,
                            onExpandedChange = { paymentMethodExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = paymentMethods.find {
                                    it.value == selectedPaymentMethod
                                }?.label ?: "",
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text("How did you pay?") },
                                leadingIcon = {
                                    Icon(
                                        paymentMethods.find {
                                            it.value == selectedPaymentMethod
                                        }?.icon ?: Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = PrimaryBlue
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = paymentMethodExpanded
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = paymentMethodExpanded,
                                onDismissRequest = { paymentMethodExpanded = false }
                            ) {
                                paymentMethods.forEach { method ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Icon(
                                                    method.icon,
                                                    contentDescription = null,
                                                    tint = PrimaryBlue,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Text(method.label)
                                            }
                                        },
                                        onClick = {
                                            selectedPaymentMethod = method.value
                                            paymentMethodExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Description field
                    Column {
                        Text(
                            text = "Description (optional)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeutralCharcoal.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = { Text("Add a note...") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Create,
                                    contentDescription = null,
                                    tint = PrimaryBlue
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error message
            if (uiState.error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SemanticRed.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = uiState.error!!,
                        color = SemanticRed,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Save Button
            Button(
                onClick = {
                    val amountLong = (amount.toDoubleOrNull() ?: 0.0).times(100).toLong()
                    if (amountLong > 0) {
                        viewModel.addExpense(
                            amount = amountLong,
                            category = selectedCategory,
                            expenseType = selectedExpenseType,
                            paymentMethod = selectedPaymentMethod,
                            description = description,
                            date = System.currentTimeMillis()
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(54.dp),
                enabled = !uiState.isLoading &&
                        amount.isNotEmpty() &&
                        selectedCategory.isNotEmpty() &&
                        selectedExpenseType.isNotEmpty() &&
                        selectedPaymentMethod.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = PrimaryBlue.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Text(
                            text = "Save Expense",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}