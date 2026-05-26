package com.example.northstar.ui.notifications

import android.app.Application
import android.content.Context
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

    // ── Core add (in-app list + system tray) ─────────────────────────────────
    fun addNotification(type: NotificationType, title: String, message: String) {
        val item = NotificationItem(
            id        = UUID.randomUUID().toString(),
            type      = type,
            title     = title,
            message   = message,
            timestamp = LocalDateTime.now(),
            isRead    = false
        )
        _notifications.update { current -> listOf(item) + current }
        NotificationHelper.post(getApplication(), item)
    }

    // ── FR18: Save timestamp every time an expense is logged ──────────────────
    fun recordExpenseTimestamp() {
        getApplication<Application>()
            .getSharedPreferences(
                ExpenseReminderReceiver.PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putLong(ExpenseReminderReceiver.KEY_LAST_EXPENSE, System.currentTimeMillis())
            .apply()
    }

    // ── Convenience helpers ───────────────────────────────────────────────────

    fun notifyIncomeLogged(amount: Double, newBalance: Double) = addNotification(
        type    = NotificationType.INCOME_LOGGED,
        title   = "Income Recorded ✅",
        message = "LKR ${fmt(amount)} income recorded! Your balance is now LKR ${fmt(newBalance)}"
    )

    fun notifyExpenseLogged(amount: Double, percentOfIncome: Int) = addNotification(
        type    = NotificationType.EXPENSE_LOGGED,
        title   = "Expense Added 🧾",
        message = "LKR ${fmt(amount)} expense added. You've spent $percentOfIncome% of your income"
    )

    fun notifyLargeExpense(amount: Double, category: String) = addNotification(
        type    = NotificationType.LARGE_EXPENSE,
        title   = "Large Expense Detected",
        message = "A large expense of LKR ${fmt(amount)} was added under $category"
    )

    fun notifyBudgetWarning(percent: Int) = addNotification(
        type    = NotificationType.BUDGET_WARNING,
        title   = "Budget Warning ⚠️",
        message = "You've used $percent% of your monthly budget"
    )

    fun notifyBudgetCritical(percent: Int) = addNotification(
        type    = NotificationType.BUDGET_CRITICAL,
        title   = "Budget Critical 🚨",
        message = "You've used $percent% of your monthly budget. Spending is critical!"
    )

    fun notifyGoalReached(goalName: String) = addNotification(
        type    = NotificationType.GOAL_REACHED,
        title   = "Goal Reached! 🎉",
        message = "Congratulations! You've reached your \"$goalName\" goal"
    )

    fun notifyGoalMilestone(goalName: String, percent: Int) = addNotification(
        type    = NotificationType.GOAL_MILESTONE,
        title   = "Goal Milestone 📊",
        message = "You're $percent% of the way to your \"$goalName\" goal. Keep it up!"
    )

    fun notifyGoalDeadline(goalName: String, daysLeft: Int, amountNeeded: Double) = addNotification(
        type    = NotificationType.GOAL_DEADLINE,
        title   = "Goal Deadline Approaching ⏰",
        message = "$goalName deadline in $daysLeft days — you need LKR ${fmt(amountNeeded)} more"
    )

    fun notifyMonthlyGoalMet() = addNotification(
        type    = NotificationType.MONTHLY_GOAL_MET,
        title   = "Monthly Goal Met! 🎉",
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

    fun notifyNoExpenseReminder() = addNotification(
        type    = NotificationType.NO_EXPENSE_REMINDER,
        title   = "Haven't logged expenses? 🧾",
        message = "You haven't recorded any expenses in 3 days. Keep your budget tracking up to date!"
    )

    // ── Read / delete ─────────────────────────────────────────────────────────
    fun markAsRead(id: String) {
        _notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    fun markAllAsRead() {
        _notifications.update { list -> list.map { it.copy(isRead = true) } }
    }

    fun deleteNotification(id: String) {
        _notifications.update { list -> list.filter { it.id != id } }
    }

    fun clearAll() {
        _notifications.update { emptyList() }
    }

    private fun fmt(amount: Double): String = "%,.0f".format(amount)
}