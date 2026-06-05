package com.example.northstar.ui.notifications

import android.Manifest
import android.app.AlarmManager
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

object NotificationHelper {

    // ── Channel IDs ──────────────────────────────────────────────────────────
    const val CHANNEL_INCOME    = "channel_income"
    const val CHANNEL_EXPENSE   = "channel_expense"
    const val CHANNEL_BUDGET    = "channel_budget"
    const val CHANNEL_GOALS     = "channel_goals"
    const val CHANNEL_REMINDERS = "channel_reminders"

    // ── Alarm request code ───────────────────────────────────────────────────
    private const val ALARM_REQUEST_CODE = 1001
    private const val INTERVAL_MS        = 24L * 60 * 60 * 1000  // 24 hours

    // ── Create all channels (call once from Application or MainActivity) ──────
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        listOf(
            NotificationChannel(
                CHANNEL_INCOME, "Income",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Income recorded notifications" },

            NotificationChannel(
                CHANNEL_EXPENSE, "Expenses",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Expense added notifications" },

            NotificationChannel(
                CHANNEL_BUDGET, "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Budget warning and critical alerts" },

            NotificationChannel(
                CHANNEL_GOALS, "Goals",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Goal milestones, deadlines and achievements" },

            NotificationChannel(
                CHANNEL_REMINDERS, "Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Periodic reminders to log expenses" }

        ).forEach { manager.createNotificationChannel(it) }
    }

    // ── FR18: Schedule repeating 24h alarm ───────────────────────────────────
    fun scheduleExpenseReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = reminderPendingIntent(context)

        // Cancel existing before re-scheduling
        alarmManager.cancel(pendingIntent)

        val triggerAtMs = System.currentTimeMillis() + INTERVAL_MS

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            alarmManager.canScheduleExactAlarms()
        ) {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerAtMs,
                INTERVAL_MS,
                pendingIntent
            )
        } else {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerAtMs,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }

    // ── FR18: Cancel alarm ───────────────────────────────────────────────────
    fun cancelExpenseReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(reminderPendingIntent(context))
    }

    // ── Build PendingIntent for the alarm ────────────────────────────────────
    private fun reminderPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, ExpenseReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    // ── Post a system notification ───────────────────────────────────────────
    fun post(context: Context, item: NotificationItem) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

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
            .setSmallIcon(smallIconFor(item.type))
            .setContentTitle(item.title)
            .setContentText(item.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(item.message))
            .setPriority(priorityFor(item.type))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(item.id.hashCode(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    // ── Channel mapping ──────────────────────────────────────────────────────
    private fun channelFor(type: NotificationType): String = when (type) {
        NotificationType.INCOME_LOGGED,
        NotificationType.MONTHLY_GOAL_MET    -> CHANNEL_INCOME

        NotificationType.EXPENSE_LOGGED,
        NotificationType.LARGE_EXPENSE       -> CHANNEL_EXPENSE

        NotificationType.BUDGET_WARNING,
        NotificationType.BUDGET_CRITICAL     -> CHANNEL_BUDGET

        NotificationType.GOAL_REACHED,
        NotificationType.GOAL_MILESTONE,
        NotificationType.GOAL_DEADLINE       -> CHANNEL_GOALS

        NotificationType.NO_INCOME_REMINDER,
        NotificationType.NO_GOAL_PROGRESS,
        NotificationType.NO_EXPENSE_REMINDER -> CHANNEL_REMINDERS
    }

    // ── Priority mapping ─────────────────────────────────────────────────────
    private fun priorityFor(type: NotificationType): Int = when (type) {
        NotificationType.BUDGET_CRITICAL     -> NotificationCompat.PRIORITY_HIGH
        NotificationType.BUDGET_WARNING,
        NotificationType.GOAL_DEADLINE,
        NotificationType.NO_EXPENSE_REMINDER -> NotificationCompat.PRIORITY_DEFAULT
        else                                 -> NotificationCompat.PRIORITY_DEFAULT
    }

    // ── Icon mapping ─────────────────────────────────────────────────────────
    private fun smallIconFor(type: NotificationType): Int = when (type) {
        NotificationType.INCOME_LOGGED,
        NotificationType.MONTHLY_GOAL_MET    -> android.R.drawable.ic_menu_add

        NotificationType.EXPENSE_LOGGED,
        NotificationType.LARGE_EXPENSE       -> android.R.drawable.ic_menu_manage

        NotificationType.BUDGET_WARNING,
        NotificationType.BUDGET_CRITICAL     -> android.R.drawable.ic_dialog_alert

        NotificationType.GOAL_REACHED,
        NotificationType.GOAL_MILESTONE,
        NotificationType.GOAL_DEADLINE       -> android.R.drawable.star_on

        NotificationType.NO_INCOME_REMINDER,
        NotificationType.NO_GOAL_PROGRESS,
        NotificationType.NO_EXPENSE_REMINDER -> android.R.drawable.ic_menu_recent_history
    }
}