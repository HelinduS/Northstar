package com.example.northstar.ui.notifications

import java.time.LocalDateTime

enum class NotificationType {
    INCOME_LOGGED, MONTHLY_GOAL_MET, NO_INCOME_REMINDER,
    EXPENSE_LOGGED, BUDGET_WARNING, BUDGET_CRITICAL, LARGE_EXPENSE,
    GOAL_REACHED, GOAL_MILESTONE, GOAL_DEADLINE, NO_GOAL_PROGRESS
}

data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val isRead: Boolean = false,
    val emoji: String = ""
)