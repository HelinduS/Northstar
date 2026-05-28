package com.example.northstar.ui.notifications

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDateTime

enum class NotificationType {
    INCOME_LOGGED,
    // Fired when a new income entry is saved successfully
    NO_INCOME_REMINDER,
    // Fired when no income has been recorded this calendar month
    EXPENSE_LOGGED,
    // Fired when a new expense entry is saved successfully
    LARGE_EXPENSE,
    // Fired when a single expense exceeds the large-expense threshold
    NO_EXPENSE_REMINDER,
    // FR18 — fired when no expenses have been logged in the past three days
    BUDGET_WARNING,
    // Fired when spending crosses the first warning threshold
    BUDGET_CRITICAL,
    // Fired when spending reaches or exceeds the critical threshold
    GOAL_REACHED,
    // Fired when the user fully achieves a savings goal
    GOAL_MILESTONE,
    // Fired when a goal crosses a significant progress percentage
    GOAL_DEADLINE,
    // Fired when a goal deadline is approaching with a remaining shortfall
    NO_GOAL_PROGRESS,
    // Fired when no contributions have been made to a goal recently
    MONTHLY_GOAL_MET
    // Fired when the user meets their overall monthly savings target
}

data class NotificationItem(
    val id        : String,
    // Unique UUID assigned at creation time
    val type      : NotificationType,
    // Category of the event that triggered this notification
    val title     : String,
    // Short bold headline displayed at the top of the notification card
    val message   : String,
    // Full description shown below the title with amounts and context
    val timestamp : LocalDateTime = LocalDateTime.now(),
    // Exact moment this notification was created, defaults to now
    val isRead    : Boolean = false,
    // Tracks whether the user has already viewed this notification
    val icon      : ImageVector = Icons.Filled.Notifications
    // Material icon shown on the leading edge of the notification card
)