import com.example.northstar.domain.model.CategoryBreakdown

data class ComparisonDataPoint(
    val label: String,
    val incomeAmount: Long,
    val expenseAmount: Long
)

data class ComparisonSummary(
    val dataPoints: List<ComparisonDataPoint>,
    val totalIncome: Long,
    val totalExpenses: Long,
    val pieBreakdown: List<CategoryBreakdown> // Used for Custom filter
)