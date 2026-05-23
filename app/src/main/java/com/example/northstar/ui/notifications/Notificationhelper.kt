package com.example.northstar.ui.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.northstar.MainActivity
import com.example.northstar.R

object NotificationHelper {

    // ── Channel IDs ──────────────────────────────────────────────────────────
    const val CHANNEL_INCOME = "channel_income"
    const val CHANNEL_EXPENSE = "channel_expense"
    const val CHANNEL_BUDGET = "channel_budget"
    const val CHANNEL_GOALS = "channel_goals"
    const val CHANNEL_REMINDERS = "channel_reminders"

    // ── Register all channels (call once from Application.onCreate) ──────────
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val channels = listOf(
            NotificationChannel(
                CHANNEL_INCOME,
                "Income",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Income recorded notifications" },

            NotificationChannel(
                CHANNEL_EXPENSE,
                "Expenses",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Expense added notifications" },

            NotificationChannel(
                CHANNEL_BUDGET,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Budget warning and critical alerts" },

            NotificationChannel(
                CHANNEL_GOALS,
                "Goals",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Goal milestones, deadlines and achievements" },

            NotificationChannel(
                CHANNEL_REMINDERS,
                "Reminders",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Periodic reminders" }
        )

        channels.forEach { manager.createNotificationChannel(it) }
    }

    // ── Map NotificationType → channel + icon ────────────────────────────────
    private fun channelFor(type: NotificationType): String = when (type) {
        NotificationType.INCOME_LOGGED,
        NotificationType.MONTHLY_GOAL_MET -> CHANNEL_INCOME

        NotificationType.EXPENSE_LOGGED,
        NotificationType.LARGE_EXPENSE -> CHANNEL_EXPENSE

        NotificationType.BUDGET_WARNING,
        NotificationType.BUDGET_CRITICAL -> CHANNEL_BUDGET

        NotificationType.GOAL_REACHED,
        NotificationType.GOAL_MILESTONE,
        NotificationType.GOAL_DEADLINE -> CHANNEL_GOALS

        NotificationType.NO_INCOME_REMINDER,
        NotificationType.NO_GOAL_PROGRESS -> CHANNEL_REMINDERS
    }

    private fun priorityFor(type: NotificationType): Int = when (type) {
        NotificationType.BUDGET_CRITICAL -> NotificationCompat.PRIORITY_HIGH
        NotificationType.BUDGET_WARNING,
        NotificationType.GOAL_DEADLINE -> NotificationCompat.PRIORITY_DEFAULT

        else -> NotificationCompat.PRIORITY_DEFAULT
    }

    // ── Post a system notification ───────────────────────────────────────────
    fun post(context: Context, item: NotificationItem) {
        // Android 13+ requires POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        // Tap → open MainActivity (deep-link to notifications screen)
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("open_notifications", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            item.id.hashCode(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelFor(item.type))
            .setSmallIcon(smallIconFor(item.type))        // monochrome vector drawable
            .setContentTitle(item.title)
            .setContentText(item.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(item.message))
            .setPriority(priorityFor(item.type))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context)
                .notify(item.id.hashCode(), notification)
        } catch (e: SecurityException) {
            // Safeguard against missing runtime permissions on edge-case API levels
            e.printStackTrace()
        }
    }

    // ── Small icon resource per type (monochrome, must be in res/drawable) ───
    // Replace the entire smallIconFor() function with this:
    private fun smallIconFor(type: NotificationType): Int = when (type) {
        NotificationType.INCOME_LOGGED,
        NotificationType.MONTHLY_GOAL_MET -> android.R.drawable.ic_menu_add

        NotificationType.EXPENSE_LOGGED,
        NotificationType.LARGE_EXPENSE -> android.R.drawable.ic_menu_manage

        NotificationType.BUDGET_WARNING,
        NotificationType.BUDGET_CRITICAL -> android.R.drawable.ic_dialog_alert

        NotificationType.GOAL_REACHED,
        NotificationType.GOAL_MILESTONE,
        NotificationType.GOAL_DEADLINE -> android.R.drawable.star_on

        NotificationType.NO_INCOME_REMINDER,
        NotificationType.NO_GOAL_PROGRESS -> android.R.drawable.ic_menu_recent_history
    }
}