package com.example.northstar.ui.history

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.northstar.ui.dashboard.DashboardViewModel
import com.example.northstar.ui.dashboard.TransactionItem
import com.example.northstar.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTransaction by remember { mutableStateOf<TransactionItem?>(null) }
    var showDeleteDialog by remember { mutableStateOf<TransactionItem?>(null) }
    val listState = rememberLazyListState()

    // View detail popup — logic unchanged
    selectedTransaction?.let { transaction ->
        TransactionDetailDialog(
            transaction = transaction,
            onDismiss = { selectedTransaction = null },
            onDelete = {
                selectedTransaction = null
                showDeleteDialog = transaction
            }
        )
    }

    // Delete confirm dialog — logic unchanged
    showDeleteDialog?.let { transaction ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            containerColor = White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    "Delete Transaction",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete this transaction? This cannot be undone.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTransaction(transaction.id, transaction.isIncome)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Debit),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = null },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transaction History",
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
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Navy900
                )
            )
        },
        containerColor = Surface
    ) { paddingValues ->

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = Navy900,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading transactions...",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
            return@Scaffold
        }

        if (uiState.recentTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Navy900.copy(alpha = 0.12f),
                                        Navy900.copy(alpha = 0.04f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📭", fontSize = 38.sp)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "No transactions yet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary,
                        letterSpacing = (-0.3).sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Start adding income and expenses\nto see them here",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── Summary Banner ────────────────────────────────────────────
            val totalIncome = uiState.recentTransactions
                .filter { it.isIncome }.sumOf { it.amount }
            val totalExpense = uiState.recentTransactions
                .filter { !it.isIncome }.sumOf { it.amount }
            val netBalance = totalIncome - totalExpense

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy900)
            ) {
                // Decorative circles for depth
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .offset(x = (-40).dp, y = (-40).dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.04f))
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 30.dp, y = (-20).dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.06f))
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Net balance — centered
                    Text(
                        text = "Net Balance",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.8.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${if (netBalance >= 0) "+ " else "- "}LKR ${
                            String.format(Locale.US, "%,.2f", Math.abs(netBalance) / 100.0)
                        }",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        letterSpacing = (-0.5).sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Income & Expense — text only, no card ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Income stat
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(9.dp)
                                        .clip(CircleShape)
                                        .background(Credit)
                                )
                                Text(
                                    text = "INCOME",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "+ LKR ${
                                    String.format(Locale.US, "%,.2f", totalIncome / 100.0)
                                }",
                                color = IncomeGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 19.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        // Vertical divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(Color.White.copy(alpha = 0.40f))
                        )

                        // Expense stat
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(9.dp)
                                        .clip(CircleShape)
                                        .background(Debit)
                                )
                                Text(
                                    text = "EXPENSES",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "- LKR ${
                                    String.format(Locale.US, "%,.2f", totalExpense / 100.0)
                                }",
                                color = ExpenseRed,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 19.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // ── Section header ────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "All Transactions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary,
                    letterSpacing = (-0.2).sp
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Navy900.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = "${uiState.recentTransactions.size} records",
                        fontSize = 11.sp,
                        color = Navy900,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // ── Transaction list ──────────────────────────────────────────
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(uiState.recentTransactions) { transaction ->
                    TransactionHistoryItem(
                        transaction = transaction,
                        onViewClick = { selectedTransaction = transaction },
                        onDeleteClick = { showDeleteDialog = transaction }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TransactionHistoryItem
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun TransactionHistoryItem(
    transaction: TransactionItem,
    onViewClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val amountText = "${if (transaction.isIncome) "+" else "-"} LKR ${
        String.format(Locale.US, "%,.2f", transaction.amount / 100.0)
    }"
    val accentColor = if (transaction.isIncome) Credit else Debit

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedDate = if (transaction.date > 0)
        dateFormat.format(Date(transaction.date)) else "—"
    val formattedTime = if (transaction.date > 0)
        timeFormat.format(Date(transaction.date)) else ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = Navy900.copy(alpha = 0.08f)
            ),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Thin accent line at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(accentColor, accentColor.copy(alpha = 0.3f))
                        ),
                        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                    )
            )

            // ── Top row: icon + title + amount ───────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Icon bubble
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        accentColor.copy(alpha = 0.18f),
                                        accentColor.copy(alpha = 0.06f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (transaction.isIncome)
                                Icons.Default.KeyboardArrowDown
                            else
                                Icons.Default.KeyboardArrowUp,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Column {
                        Text(
                            text = transaction.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary,
                            letterSpacing = (-0.2).sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        // Date + time under title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = formattedDate,
                                fontSize = 11.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Medium
                            )
                            if (formattedTime.isNotEmpty()) {
                                Text(
                                    text = "·",
                                    fontSize = 11.sp,
                                    color = TextHint
                                )
                                Text(
                                    text = formattedTime,
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Amount chip
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accentColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = amountText,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // ── Info chip row: category · type · payment ─────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = accentColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = transaction.category,
                        fontSize = 10.sp,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.2.sp
                    )
                }

                // Expense type
                if (!transaction.isIncome && transaction.expenseType.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Navy900.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = transaction.expenseType,
                            fontSize = 10.sp,
                            color = Navy900,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.2.sp
                        )
                    }
                }

                // Payment method
                if (!transaction.isIncome && transaction.paymentMethod.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ChipBg
                    ) {
                        Text(
                            text = transaction.paymentMethod,
                            fontSize = 10.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.2.sp
                        )
                    }
                }
            }

            // Divider
            HorizontalDivider(
                color = ListDivider,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Action row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onViewClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = Navy900),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "View",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        "Details",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                TextButton(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = Debit),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        "Delete",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TransactionDetailDialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun TransactionDetailDialog(
    transaction: TransactionItem,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val formattedDate = if (transaction.date > 0)
        dateFormat.format(Date(transaction.date))
    else "N/A"

    val accentColor = if (transaction.isIncome) Credit else Debit

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                // ── Header ─────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Navy900,
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Decorative background circle
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 30.dp, y = (-30).dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.06f))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Icon circle
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.28f),
                                            Color.White.copy(alpha = 0.10f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (transaction.isIncome)
                                    Icons.Default.KeyboardArrowDown
                                else
                                    Icons.Default.KeyboardArrowUp,
                                contentDescription = null,
                                tint = if (transaction.isIncome) IncomeGreen else Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))

                        // Type pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = accentColor
                        ) {
                            Text(
                                text = if (transaction.isIncome) "● INCOME" else "● EXPENSE",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp,
                                modifier = Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 5.dp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${if (transaction.isIncome) "+" else "-"} LKR ${
                                String.format(
                                    Locale.US, "%,.2f",
                                    transaction.amount / 100.0
                                )
                            }",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = transaction.title,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // ── Details ────────────────────────────────────────────────
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    DetailRow(label = "Date", value = formattedDate)
                    DetailDivider()
                    DetailRow(label = "Category", value = transaction.category)

                    if (!transaction.isIncome) {
                        if (transaction.expenseType.isNotEmpty()) {
                            DetailDivider()
                            DetailRow(label = "Type", value = transaction.expenseType)
                        }
                        if (transaction.paymentMethod.isNotEmpty()) {
                            DetailDivider()
                            DetailRow(label = "Payment", value = transaction.paymentMethod)
                        }
                        if (transaction.description.isNotEmpty()) {
                            DetailDivider()
                            DetailRow(
                                label = "Description",
                                value = transaction.description
                            )
                        }
                    } else {
                        if (transaction.originalCurrency != "LKR") {
                            DetailDivider()
                            DetailRow(
                                label = "Original Amount",
                                value = "${transaction.originalCurrency} ${
                                    String.format(
                                        Locale.US, "%,.2f",
                                        transaction.originalAmount / 100.0
                                    )
                                }"
                            )
                            DetailDivider()
                            DetailRow(
                                label = "Exchange Rate",
                                value = "1 ${transaction.originalCurrency} = LKR ${
                                    String.format(
                                        Locale.US, "%.2f",
                                        transaction.exchangeRate
                                    )
                                }"
                            )
                        }
                        if (transaction.notes.isNotEmpty()) {
                            DetailDivider()
                            DetailRow(label = "Notes", value = transaction.notes)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TextPrimary
                            )
                        ) {
                            Text("Close", fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = onDelete,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Debit
                            ),
                            shape = RoundedCornerShape(14.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp
                            )
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Delete", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DetailRow & DetailDivider
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DetailDivider() {
    HorizontalDivider(
        color = Separator,
        thickness = 0.8.dp,
        modifier = Modifier.padding(vertical = 10.dp)
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.6f),
            textAlign = TextAlign.End,
            letterSpacing = (-0.1).sp
        )
    }
}