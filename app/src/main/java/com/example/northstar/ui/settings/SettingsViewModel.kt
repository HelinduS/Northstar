package com.example.northstar.ui.settings

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.northstar.ui.theme.ThemePreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = false,
    val exportSuccess: Boolean = false,
    val clearSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context,
    private val themePreferenceManager: ThemePreferenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val isDarkMode: StateFlow<Boolean> = themePreferenceManager.isDarkMode

    fun setDarkMode(enabled: Boolean) {
        themePreferenceManager.setDarkMode(enabled)
    }

    fun exportData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val user = firebaseAuth.currentUser ?: return@launch
                val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

                // Fetch all data
                val incomesSnapshot = firestore
                    .collection("users")
                    .document(user.uid)
                    .collection("incomes")
                    .get()
                    .await()

                val expensesSnapshot = firestore
                    .collection("users")
                    .document(user.uid)
                    .collection("expenses")
                    .get()
                    .await()

                val goalsSnapshot = firestore
                    .collection("users")
                    .document(user.uid)
                    .collection("goals")
                    .get()
                    .await()

                // Create PDF
                val pdfDocument = PdfDocument()
                val paint = Paint()
                val titlePaint = Paint()
                val headerPaint = Paint()

                val pageWidth = 595
                val pageHeight = 842
                var pageNumber = 1
                var yPos = 60f

                fun newPage(): PdfDocument.Page {
                    val pageInfo = PdfDocument.PageInfo.Builder(
                        pageWidth, pageHeight, pageNumber++
                    ).create()
                    return pdfDocument.startPage(pageInfo)
                }

                fun drawHeader(canvas: android.graphics.Canvas) {
                    paint.color = android.graphics.Color.parseColor("#0D1117")
                    canvas.drawRect(0f, 0f, pageWidth.toFloat(), 80f, paint)

                    titlePaint.color = android.graphics.Color.WHITE
                    titlePaint.textSize = 22f
                    titlePaint.isFakeBoldText = true
                    titlePaint.typeface = Typeface.DEFAULT_BOLD
                    titlePaint.textAlign = Paint.Align.CENTER
                    canvas.drawText(
                        "NorthStar Financial Report",
                        pageWidth / 2f, 40f, titlePaint
                    )

                    titlePaint.textSize = 11f
                    titlePaint.isFakeBoldText = false
                    titlePaint.color = android.graphics.Color.parseColor("#9CA3AF")
                    titlePaint.textAlign = Paint.Align.CENTER
                    canvas.drawText(
                        "Generated: ${dateFormat.format(Date())}   |   ${user.email}",
                        pageWidth / 2f, 62f, titlePaint
                    )
                }

                fun drawSectionTitle(
                    canvas: android.graphics.Canvas,
                    title: String,
                    y: Float
                ): Float {
                    headerPaint.color = android.graphics.Color.parseColor("#0D1117")
                    headerPaint.textSize = 13f
                    headerPaint.isFakeBoldText = true
                    headerPaint.textAlign = Paint.Align.LEFT
                    paint.color = android.graphics.Color.parseColor("#F3F4F6")
                    canvas.drawRect(
                        28f, y,
                        (pageWidth - 12).toFloat(), y + 28f, paint
                    )
                    canvas.drawText(title, 36f, y + 19f, headerPaint)
                    return y + 38f
                }

                fun drawRow(
                    canvas: android.graphics.Canvas,
                    label: String,
                    value: String,
                    y: Float,
                    isAlternate: Boolean = false
                ): Float {
                    if (isAlternate) {
                        paint.color = android.graphics.Color.parseColor("#F9FAFB")
                        canvas.drawRect(
                            28f, y - 4f,
                            (pageWidth - 12).toFloat(), y + 18f, paint
                        )
                    }
                    val labelPaint = Paint().apply {
                        color = android.graphics.Color.parseColor("#6B7280")
                        textSize = 10f
                        textAlign = Paint.Align.LEFT
                    }
                    val valuePaint = Paint().apply {
                        color = android.graphics.Color.parseColor("#111827")
                        textSize = 10f
                        isFakeBoldText = true
                        textAlign = Paint.Align.LEFT
                    }

                    canvas.drawText(label, 36f, y + 10f, labelPaint)

                    val valueWidth = valuePaint.measureText(value)
                    canvas.drawText(
                        value,
                        (pageWidth - 20f) - valueWidth,
                        y + 10f,
                        valuePaint
                    )

                    val dotPaint = Paint().apply {
                        color = android.graphics.Color.parseColor("#E5E7EB")
                        textSize = 10f
                    }
                    val labelWidth = labelPaint.measureText(label)
                    canvas.drawText(
                        "·".repeat(40),
                        36f + labelWidth + 4f,
                        y + 10f,
                        dotPaint
                    )

                    return y + 22f
                }

                // Page 1
                var page = newPage()
                var canvas = page.canvas
                drawHeader(canvas)
                yPos = 100f

                // Income section
                yPos = drawSectionTitle(canvas, "INCOME RECORDS", yPos)
                var totalIncome = 0L
                var rowIndex = 0

                incomesSnapshot.documents.forEach { doc ->
                    if (yPos > pageHeight - 60f) {
                        pdfDocument.finishPage(page)
                        page = newPage()
                        canvas = page.canvas
                        drawHeader(canvas)
                        yPos = 100f
                    }
                    val amount = doc.getLong("lkrAmount") ?: 0L
                    val source = doc.getString("sourceType") ?: "Unknown"
                    val date = doc.getTimestamp("date")?.toDate()
                    val notes = doc.getString("notes") ?: "-"
                    totalIncome += amount

                    yPos = drawRow(canvas, "Source", source, yPos, rowIndex % 2 == 0)
                    yPos = drawRow(
                        canvas, "Amount",
                        "LKR ${String.format(Locale.US, "%,.2f", amount / 100.0)}",
                        yPos, rowIndex % 2 == 0
                    )
                    yPos = drawRow(
                        canvas, "Date",
                        if (date != null) dateFormat.format(date) else "-",
                        yPos, rowIndex % 2 == 0
                    )
                    yPos = drawRow(
                        canvas, "Notes",
                        notes.ifEmpty { "-" },
                        yPos, rowIndex % 2 == 0
                    )

                    paint.color = android.graphics.Color.parseColor("#E5E7EB")
                    paint.strokeWidth = 0.5f
                    canvas.drawLine(
                        28f, yPos,
                        (pageWidth - 12).toFloat(), yPos, paint
                    )
                    yPos += 8f
                    rowIndex++
                }

                // Total income
                paint.color = android.graphics.Color.parseColor("#DCFCE7")
                canvas.drawRect(
                    28f, yPos,
                    (pageWidth - 12).toFloat(), yPos + 28f, paint
                )
                val totalPaint = Paint().apply {
                    color = android.graphics.Color.parseColor("#16A34A")
                    textSize = 12f
                    isFakeBoldText = true
                    textAlign = Paint.Align.LEFT
                }
                canvas.drawText("Total Income", 36f, yPos + 19f, totalPaint)
                val totalIncomeText = "LKR ${String.format(Locale.US, "%,.2f", totalIncome / 100.0)}"
                val tiWidth = totalPaint.measureText(totalIncomeText)
                canvas.drawText(totalIncomeText, (pageWidth - 20f) - tiWidth, yPos + 19f, totalPaint)
                yPos += 40f

                // Expense section
                if (yPos > pageHeight - 100f) {
                    pdfDocument.finishPage(page)
                    page = newPage()
                    canvas = page.canvas
                    drawHeader(canvas)
                    yPos = 100f
                }

                yPos = drawSectionTitle(canvas, "EXPENSE RECORDS", yPos)
                var totalExpenses = 0L
                rowIndex = 0

                expensesSnapshot.documents.forEach { doc ->
                    if (yPos > pageHeight - 60f) {
                        pdfDocument.finishPage(page)
                        page = newPage()
                        canvas = page.canvas
                        drawHeader(canvas)
                        yPos = 100f
                    }
                    val amount = doc.getLong("amount") ?: 0L
                    val category = doc.getString("category") ?: "Unknown"
                    val expenseType = doc.getString("expenseType") ?: "-"
                    val paymentMethod = doc.getString("paymentMethod") ?: "-"
                    val description = doc.getString("description") ?: "-"
                    val date = doc.getTimestamp("date")?.toDate()
                    totalExpenses += amount

                    yPos = drawRow(canvas, "Category", category, yPos, rowIndex % 2 == 0)
                    yPos = drawRow(
                        canvas, "Amount",
                        "LKR ${String.format(Locale.US, "%,.2f", amount / 100.0)}",
                        yPos, rowIndex % 2 == 0
                    )
                    yPos = drawRow(canvas, "Type", expenseType, yPos, rowIndex % 2 == 0)
                    yPos = drawRow(canvas, "Payment", paymentMethod, yPos, rowIndex % 2 == 0)
                    yPos = drawRow(
                        canvas, "Description",
                        description.ifEmpty { "-" },
                        yPos, rowIndex % 2 == 0
                    )
                    yPos = drawRow(
                        canvas, "Date",
                        if (date != null) dateFormat.format(date) else "-",
                        yPos, rowIndex % 2 == 0
                    )

                    paint.color = android.graphics.Color.parseColor("#E5E7EB")
                    paint.strokeWidth = 0.5f
                    canvas.drawLine(
                        28f, yPos,
                        (pageWidth - 12).toFloat(), yPos, paint
                    )
                    yPos += 8f
                    rowIndex++
                }

                // Total expenses
                paint.color = android.graphics.Color.parseColor("#FEE2E2")
                canvas.drawRect(
                    28f, yPos,
                    (pageWidth - 12).toFloat(), yPos + 28f, paint
                )
                val expTotalPaint = Paint().apply {
                    color = android.graphics.Color.parseColor("#DC2626")
                    textSize = 12f
                    isFakeBoldText = true
                    textAlign = Paint.Align.LEFT
                }
                canvas.drawText("Total Expenses", 36f, yPos + 19f, expTotalPaint)
                val totalExpText = "LKR ${String.format(Locale.US, "%,.2f", totalExpenses / 100.0)}"
                val teWidth = expTotalPaint.measureText(totalExpText)
                canvas.drawText(totalExpText, (pageWidth - 20f) - teWidth, yPos + 19f, expTotalPaint)
                yPos += 40f

                // Goals section
                if (yPos > pageHeight - 100f) {
                    pdfDocument.finishPage(page)
                    page = newPage()
                    canvas = page.canvas
                    drawHeader(canvas)
                    yPos = 100f
                }

                yPos = drawSectionTitle(canvas, "SAVINGS GOALS", yPos)
                rowIndex = 0

                goalsSnapshot.documents.forEach { doc ->
                    if (yPos > pageHeight - 60f) {
                        pdfDocument.finishPage(page)
                        page = newPage()
                        canvas = page.canvas
                        drawHeader(canvas)
                        yPos = 100f
                    }
                    val name = doc.getString("name") ?: "Unknown"
                    val targetAmount = doc.getLong("targetAmount") ?: 0L
                    val savedAmount = doc.getLong("savedAmount") ?: 0L
                    val targetDate = doc.getTimestamp("targetDate")?.toDate()
                    val progress = if (targetAmount > 0)
                        (savedAmount * 100 / targetAmount) else 0

                    yPos = drawRow(canvas, "Goal", name, yPos, rowIndex % 2 == 0)
                    yPos = drawRow(
                        canvas, "Target",
                        "LKR ${String.format(Locale.US, "%,.2f", targetAmount / 100.0)}",
                        yPos, rowIndex % 2 == 0
                    )
                    yPos = drawRow(
                        canvas, "Saved",
                        "LKR ${String.format(Locale.US, "%,.2f", savedAmount / 100.0)}",
                        yPos, rowIndex % 2 == 0
                    )
                    yPos = drawRow(canvas, "Progress", "$progress%", yPos, rowIndex % 2 == 0)
                    if (targetDate != null) {
                        yPos = drawRow(
                            canvas, "Target Date",
                            dateFormat.format(targetDate),
                            yPos, rowIndex % 2 == 0
                        )
                    }

                    paint.color = android.graphics.Color.parseColor("#E5E7EB")
                    paint.strokeWidth = 0.5f
                    canvas.drawLine(
                        28f, yPos,
                        (pageWidth - 12).toFloat(), yPos, paint
                    )
                    yPos += 8f
                    rowIndex++
                }

                // Summary section
                if (yPos > pageHeight - 140f) {
                    pdfDocument.finishPage(page)
                    page = newPage()
                    canvas = page.canvas
                    drawHeader(canvas)
                    yPos = 100f
                }

                yPos = drawSectionTitle(canvas, "FINANCIAL SUMMARY", yPos)
                val netSaved = totalIncome - totalExpenses

                yPos = drawRow(
                    canvas, "Total Income",
                    "LKR ${String.format(Locale.US, "%,.2f", totalIncome / 100.0)}",
                    yPos, false
                )
                yPos = drawRow(
                    canvas, "Total Expenses",
                    "LKR ${String.format(Locale.US, "%,.2f", totalExpenses / 100.0)}",
                    yPos, true
                )

                paint.color = if (netSaved >= 0)
                    android.graphics.Color.parseColor("#DCFCE7")
                else
                    android.graphics.Color.parseColor("#FEE2E2")
                canvas.drawRect(
                    28f, yPos,
                    (pageWidth - 12).toFloat(), yPos + 30f, paint
                )
                val netPaint = Paint().apply {
                    color = if (netSaved >= 0)
                        android.graphics.Color.parseColor("#16A34A")
                    else
                        android.graphics.Color.parseColor("#DC2626")
                    textSize = 13f
                    isFakeBoldText = true
                    textAlign = Paint.Align.LEFT
                }
                canvas.drawText("Net Saved", 36f, yPos + 21f, netPaint)
                val netText = "LKR ${String.format(Locale.US, "%,.2f", Math.abs(netSaved) / 100.0)}"
                val netWidth = netPaint.measureText(netText)
                canvas.drawText(netText, (pageWidth - 20f) - netWidth, yPos + 21f, netPaint)

                pdfDocument.finishPage(page)

                // Save PDF
                val fileName = "northstar_report_${System.currentTimeMillis()}.pdf"
                val file = File(context.getExternalFilesDir(null), fileName)
                pdfDocument.writeTo(file.outputStream())
                pdfDocument.close()

                // Share PDF
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "NorthStar Financial Report")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(
                    Intent.createChooser(shareIntent, "Export as PDF")
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    exportSuccess = true
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to export data"
                )
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val user = firebaseAuth.currentUser ?: return@launch

                val incomes = firestore
                    .collection("users")
                    .document(user.uid)
                    .collection("incomes")
                    .get()
                    .await()
                incomes.documents.forEach { it.reference.delete().await() }

                val expenses = firestore
                    .collection("users")
                    .document(user.uid)
                    .collection("expenses")
                    .get()
                    .await()
                expenses.documents.forEach { it.reference.delete().await() }

                val goals = firestore
                    .collection("users")
                    .document(user.uid)
                    .collection("goals")
                    .get()
                    .await()
                goals.documents.forEach { it.reference.delete().await() }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    clearSuccess = true
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to clear data"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = SettingsUiState()
    }
}