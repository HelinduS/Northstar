package com.example.northstar.ui.notifications

import android.app.Application
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.util.UUID

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    // Read-only state exposed to the UI
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Count of notifications not yet seen by the user
    val unreadCount: Int get() = _notifications.value.count { !it.isRead }

    // Builds a NotificationItem, prepends it to the list, and posts to system tray
    fun addNotification(
        type    : NotificationType,
        title   : String,
        message : String,
        icon    : ImageVector = Icons.Filled.Notifications
    ) {
        val item = NotificationItem(
            id        = UUID.randomUUID().toString(),
            type      = type,
            title     = title,
            message   = message,
            timestamp = LocalDateTime.now(),
            isRead    = false,
            icon      = icon
        )
        _notifications.update { current -> listOf(item) + current }
        NotificationHelper.post(getApplication(), item)
    }

    // FR18: Saves the timestamp of the last logged expense for reminder scheduling
    fun recordExpenseTimestamp() {
        getApplication<Application>()
            .getSharedPreferences(ExpenseReminderReceiver.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(ExpenseReminderReceiver.KEY_LAST_EXPENSE, System.currentTimeMillis())
            .apply()
    }

    // Notify helpers — each one maps to a specific app event

    fun notifyIncomeLogged(amount: Double, newBalance: Double) = addNotification(
        type    = NotificationType.INCOME_LOGGED,
        title   = "Income Recorded",
        message = "LKR ${fmt(amount)} income recorded. Your balance is now LKR ${fmt(newBalance)}",
        icon    = Icons.Filled.AddCircle
    )

    fun notifyExpenseLogged(amount: Double, percentOfIncome: Int) = addNotification(
        type    = NotificationType.EXPENSE_LOGGED,
        title   = "Expense Added",
        message = "LKR ${fmt(amount)} expense added. You have spent $percentOfIncome% of your income",
        icon    = Icons.Filled.Receipt
    )

    fun notifyLargeExpense(amount: Double, category: String) = addNotification(
        type    = NotificationType.LARGE_EXPENSE,
        title   = "Large Expense Detected",
        message = "A large expense of LKR ${fmt(amount)} was added under $category",
        icon    = Icons.Filled.Payments
    )

    fun notifyBudgetWarning(percent: Int) = addNotification(
        type    = NotificationType.BUDGET_WARNING,
        title   = "Budget Warning",
        message = "You have used $percent% of your monthly budget",
        icon    = Icons.Filled.Warning
    )

    fun notifyBudgetCritical(percent: Int) = addNotification(
        type    = NotificationType.BUDGET_CRITICAL,
        title   = "Budget Critical",
        message = "You have used $percent% of your monthly budget. Spending is critical!",
        icon    = Icons.Filled.NotificationsActive
    )

    fun notifyGoalReached(goalName: String) = addNotification(
        type    = NotificationType.GOAL_REACHED,
        title   = "Goal Reached",
        message = "Congratulations! You have reached your \"$goalName\" goal",
        icon    = Icons.Filled.EmojiEvents
    )

    fun notifyGoalMilestone(goalName: String, percent: Int) = addNotification(
        type    = NotificationType.GOAL_MILESTONE,
        title   = "Goal Milestone",
        message = "You are $percent% of the way to your \"$goalName\" goal. Keep it up!",
        icon    = Icons.Filled.ShowChart
    )

    fun notifyGoalDeadline(goalName: String, daysLeft: Int, amountNeeded: Double) = addNotification(
        type    = NotificationType.GOAL_DEADLINE,
        title   = "Goal Deadline Approaching",
        message = "$goalName deadline in $daysLeft days — you need LKR ${fmt(amountNeeded)} more",
        icon    = Icons.Filled.CalendarMonth
    )

    fun notifyMonthlyGoalMet() = addNotification(
        type    = NotificationType.MONTHLY_GOAL_MET,
        title   = "Monthly Goal Met",
        message = "You have met your monthly savings goal. Great discipline this month!",
        icon    = Icons.Filled.Savings
    )

    fun notifyNoIncomeReminder() = addNotification(
        type    = NotificationType.NO_INCOME_REMINDER,
        title   = "No Income This Month",
        message = "You have not logged any income yet this month. Do not forget to record it!",
        icon    = Icons.Filled.AccountBalanceWallet
    )

    fun notifyNoGoalProgress(goalName: String) = addNotification(
        type    = NotificationType.NO_GOAL_PROGRESS,
        title   = "Goal Needs Attention",
        message = "No progress on \"$goalName\" recently. A small contribution goes a long way!",
        icon    = Icons.Filled.ShowChart
    )

    fun notifyNoExpenseReminder() = addNotification(
        type    = NotificationType.NO_EXPENSE_REMINDER,
        title   = "Expenses Not Logged",
        message = "You have not recorded any expenses in 3 days. Keep your budget tracking up to date!",
        icon    = Icons.Filled.Receipt
    )

    // Mark a single notification as read by ID
    fun markAsRead(id: String) {
        _notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    // Mark all notifications as read
    fun markAllAsRead() {
        _notifications.update { list -> list.map { it.copy(isRead = true) } }
    }

    // Remove a single notification by ID
    fun deleteNotification(id: String) {
        _notifications.update { list -> list.filter { it.id != id } }
    }

    // Remove all notifications
    fun clearAll() {
        _notifications.update { emptyList() }
    }

    // Formats a Double into a comma-separated string e.g. 1234567.0 → "1,234,567"
    private fun fmt(amount: Double): String = "%,.0f".format(amount)
}