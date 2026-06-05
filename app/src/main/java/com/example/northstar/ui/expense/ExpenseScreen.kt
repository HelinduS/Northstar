package com.example.northstar.ui.expense

import android.app.DatePickerDialog
import android.view.ContextThemeWrapper
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.northstar.ui.dashboard.DashboardViewModel
import com.example.northstar.ui.notifications.NotificationViewModel
import com.example.northstar.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.clickable
data class CategoryOption(
    val value: String,
    val label: String,
    val icon: ImageVector
)

data class OptionItem(
    val value: String,
    val label: String,
    val icon: ImageVector
)

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
    viewModel: ExpenseViewModel = hiltViewModel(),
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val uiState by viewModel.uiState.collectAsState()
    val dashboardUiState by dashboardViewModel.uiState.collectAsState()

    val context = LocalContext.current

    val notificationViewModel: NotificationViewModel = viewModel(
        viewModelStoreOwner = context as ComponentActivity
    )

    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedExpenseType by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableLongStateOf(0L) }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    var categoryExpanded by remember { mutableStateOf(false) }
    var expenseTypeExpanded by remember { mutableStateOf(false) }
    var paymentMethodExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            val amountDouble = uiState.savedAmount / 100.0

            notificationViewModel.recordExpenseTimestamp()

            val totalIncomeLkr: Long = dashboardUiState.totalIncomeLkr

            val percent = if (totalIncomeLkr > 0) {
                ((uiState.savedAmount * 100L) / totalIncomeLkr).toInt()
            } else {
                0
            }

            notificationViewModel.notifyExpenseLogged(amountDouble, percent)

            if (uiState.isLargeExpense) {
                notificationViewModel.notifyLargeExpense(amountDouble, uiState.savedCategory)
            }

            if (totalIncomeLkr > 0) {
                val totalExpenses = uiState.totalExpensesLkr
                val budgetPercent = ((totalExpenses * 100L) / totalIncomeLkr).toInt()
                when {
                    budgetPercent >= 90 -> notificationViewModel.notifyBudgetCritical(budgetPercent)
                    budgetPercent >= 70 -> notificationViewModel.notifyBudgetWarning(budgetPercent)
                }
            }

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
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = (-0.3).sp,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(36.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenDeep)
            )
        },
        containerColor = cs.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            // HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = GreenDeep)
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 40.dp, y = (-30).dp)
                        .background(
                            color = Color.White.copy(alpha = 0.05f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "HOW MUCH DID YOU SPEND?",
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "LKR",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(end = 8.dp, top = 10.dp)
                        )
                        Text(
                            text = if (amount.isEmpty()) "0.00" else amount,
                            color = Color.White,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1).sp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        placeholder = {
                            Text(
                                "Enter amount",
                                color = Color.White.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White.copy(alpha = 0.7f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                            cursorColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // DETAILS CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = cs.primary.copy(alpha = 0.08f)
                    ),
                colors = CardDefaults.cardColors(containerColor = cs.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(
                                    color = Debit,
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                        )
                        Text(
                            text = "Expense Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = cs.onSurface,
                            letterSpacing = (-0.2).sp
                        )
                    }

                    // Category dropdown
                    DropdownField(
                        label = "Category",
                        value = expenseCategories.find { it.value == selectedCategory }?.label ?: "",
                        placeholder = "Select a category",
                        leadingIcon = expenseCategories.find { it.value == selectedCategory }?.icon
                            ?: Icons.Default.List,
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it }
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
                                            tint = cs.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            category.label,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = cs.onSurface
                                        )
                                    }
                                },
                                onClick = {
                                    selectedCategory = category.value
                                    categoryExpanded = false
                                }
                            )
                        }
                    }

                    HorizontalDivider(color = cs.outlineVariant, thickness = 0.8.dp)

                    // Expense Type dropdown
                    DropdownField(
                        label = "Expense Type",
                        value = expenseTypes.find { it.value == selectedExpenseType }?.label ?: "",
                        placeholder = "Committed or Discretionary?",
                        leadingIcon = expenseTypes.find { it.value == selectedExpenseType }?.icon
                            ?: Icons.Default.List,
                        expanded = expenseTypeExpanded,
                        onExpandedChange = { expenseTypeExpanded = it }
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
                                            tint = cs.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Column {
                                            Text(
                                                text = type.label,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 14.sp,
                                                color = cs.onSurface
                                            )
                                            Text(
                                                text = if (type.value == "COMMITTED")
                                                    "Fixed recurring (rent, subscriptions)"
                                                else
                                                    "Variable optional (dining, shopping)",
                                                fontSize = 11.sp,
                                                color = cs.onSurfaceVariant
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

                    HorizontalDivider(color = cs.outlineVariant, thickness = 0.8.dp)

                    // Payment Method dropdown
                    DropdownField(
                        label = "Payment Method",
                        value = paymentMethods.find { it.value == selectedPaymentMethod }?.label ?: "",
                        placeholder = "How did you pay?",
                        leadingIcon = paymentMethods.find { it.value == selectedPaymentMethod }?.icon
                            ?: Icons.Default.Edit,
                        expanded = paymentMethodExpanded,
                        onExpandedChange = { paymentMethodExpanded = it }
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
                                            tint = cs.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            method.label,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = cs.onSurface
                                        )
                                    }
                                },
                                onClick = {
                                    selectedPaymentMethod = method.value
                                    paymentMethodExpanded = false
                                }
                            )
                        }
                    }

                    HorizontalDivider(color = cs.outlineVariant, thickness = 0.8.dp)

                    // Date picker
                    Column {
                        Text(
                            text = "Date",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = cs.onSurfaceVariant,
                            letterSpacing = 0.3.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        val cal = Calendar.getInstance()
                        val openDatePicker = {
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
                        OutlinedTextField(
                            value = if (selectedDate == 0L) ""
                            else dateFormatter.format(Date(selectedDate)),
                            onValueChange = {},
                            readOnly = true,
                            placeholder = {
                                Text(
                                    "Select date",
                                    color = cs.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = if (selectedDate == 0L) cs.onSurfaceVariant else cs.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Pick date",
                                    tint = cs.onSurfaceVariant
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { openDatePicker() },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = cs.primary.copy(alpha = 0.5f),
                                unfocusedBorderColor = cs.outline,
                                disabledBorderColor = cs.outline,
                                disabledTextColor = cs.onSurface,
                                disabledLeadingIconColor = if (selectedDate == 0L) cs.onSurfaceVariant else cs.primary,
                                disabledTrailingIconColor = cs.onSurfaceVariant,
                                disabledPlaceholderColor = cs.onSurfaceVariant
                            ),
                            enabled = false
                        )
                    }

                    HorizontalDivider(color = cs.outlineVariant, thickness = 0.8.dp)

                    // Description field
                    Column {
                        Text(
                            text = "Description",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = cs.onSurfaceVariant,
                            letterSpacing = 0.3.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = {
                                Text(
                                    "Add a note (optional)",
                                    color = cs.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Create,
                                    contentDescription = null,
                                    tint = cs.onSurfaceVariant
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = cs.primary.copy(alpha = 0.5f),
                                unfocusedBorderColor = cs.outline
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Debit.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = uiState.error!!,
                        color = Debit,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

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
                            date = if (selectedDate == 0L) System.currentTimeMillis()
                            else selectedDate
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
                        selectedPaymentMethod.isNotEmpty() &&
                        selectedDate != 0L,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenDeep,
                    disabledContainerColor = GreenDeep.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Text(
                            text = "Save Expense",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    value: String,
    placeholder: String,
    leadingIcon: ImageVector,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = cs.onSurfaceVariant,
            letterSpacing = 0.3.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                placeholder = {
                    Text(placeholder, color = cs.onSurfaceVariant, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(
                        leadingIcon,
                        contentDescription = null,
                        tint = if (value.isEmpty()) cs.onSurfaceVariant else cs.primary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = cs.onSurface,
                    unfocusedTextColor = cs.onSurface,
                    focusedBorderColor = cs.primary.copy(alpha = 0.5f),
                    unfocusedBorderColor = cs.outline
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                content()
            }
        }
    }
}