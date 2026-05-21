package com.example.northstar.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.util.UUID



class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    val unreadCount: Int get() = _notifications.value.count { !it.isRead }

    // ── Add a notification (in-app list + system tray) ───────────────────────

    fun addNotification(
        type: NotificationType,
        title: String,
        message: String
    ) {
        val item = NotificationItem(
            id        = UUID.randomUUID().toString(),
            type      = type,
            title     = title,
            message   = message,
            timestamp = LocalDateTime.now(),
            isRead    = false
        )

        // 1. Add to in-app list
        _notifications.update { current -> listOf(item) + current }

        // 2. Post to Android notification tray
        NotificationHelper.post(getApplication(), item)
    }

    // ── Convenience helpers (call these from income/expense/goals logic) ─────

    fun notifyIncomeLogged(amount: Double, newBalance: Double) = addNotification(
        type    = NotificationType.INCOME_LOGGED,
        title   = "Income Recorded",
        message = "LKR ${formatAmount(amount)} income recorded! Your balance is now LKR ${formatAmount(newBalance)}"
    )

    fun notifyExpenseLogged(amount: Double, percentOfIncome: Int) = addNotification(
        type    = NotificationType.EXPENSE_LOGGED,
        title   = "Expense Added",
        message = "LKR ${formatAmount(amount)} expense added. You've spent $percentOfIncome% of your income"
    )

    fun notifyLargeExpense(amount: Double, category: String) = addNotification(
        type    = NotificationType.LARGE_EXPENSE,
        title   = "Large Expense Detected",
        message = "A large expense of LKR ${formatAmount(amount)} was added under $category"
    )

    fun notifyBudgetWarning(percent: Int) = addNotification(
        type    = NotificationType.BUDGET_WARNING,
        title   = "Budget Warning",
        message = "You've used $percent% of your monthly budget"
    )

    fun notifyBudgetCritical(percent: Int) = addNotification(
        type    = NotificationType.BUDGET_CRITICAL,
        title   = "Budget Critical",
        message = "⚠ You've used $percent% of your monthly budget. Spending is critical!"
    )

    fun notifyGoalReached(goalName: String) = addNotification(
        type    = NotificationType.GOAL_REACHED,
        title   = "Goal Reached! 🎉",
        message = "Congratulations! You've reached your \"$goalName\" goal"
    )

    fun notifyGoalMilestone(goalName: String, percent: Int) = addNotification(
        type    = NotificationType.GOAL_MILESTONE,
        title   = "Goal Milestone",
        message = "You're $percent% of the way to your \"$goalName\" goal. Keep it up!"
    )

    fun notifyGoalDeadline(goalName: String, daysLeft: Int, amountNeeded: Double) = addNotification(
        type    = NotificationType.GOAL_DEADLINE,
        title   = "Goal Deadline Approaching",
        message = "$goalName deadline in $daysLeft days — you need LKR ${formatAmount(amountNeeded)} more"
    )

    fun notifyMonthlyGoalMet() = addNotification(
        type    = NotificationType.MONTHLY_GOAL_MET,
        title   = "Monthly Goal Met!",
        message = "You've met your monthly savings goal. Great discipline this month!"
    )

    fun notifyNoIncomeReminder() = addNotification(
        type    = NotificationType.NO_INCOME_REMINDER,
        title   = "No Income This Month",
        message = "You haven't logged any income yet this month. Don't forget to record it!"
    )

    fun notifyNoGoalProgress(goalName: String) = addNotification(
        type    = NotificationType.NO_GOAL_PROGRESS,
        title   = "Goal Needs Attention",
        message = "No progress on \"$goalName\" recently. A small contribution goes a long way!"
    )

    // ── Read / delete ────────────────────────────────────────────────────────

    fun markRead(id: String) {
        _notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    fun markAllRead() {
        _notifications.update { list -> list.map { it.copy(isRead = true) } }
    }

    fun delete(id: String) {
        _notifications.update { list -> list.filter { it.id != id } }
    }

    fun clearAll() {
        _notifications.update { emptyList() }
    }



    // ── Util ─────────────────────────────────────────────────────────────────

    private fun formatAmount(amount: Double): String =
        "%,.0f".format(amount)
}