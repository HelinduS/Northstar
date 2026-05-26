package com.example.northstar.ui.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.northstar.MainActivity
import java.util.UUID

class ExpenseReminderReceiver : BroadcastReceiver() {

    companion object {
        const val PREFS_NAME        = "northstar_expense_prefs"
        const val KEY_LAST_EXPENSE  = "last_expense_timestamp_ms"
        const val THREE_DAYS_MS     =3L * 24 * 60 * 60 * 1000
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {

            // ── Device rebooted → re-schedule the alarm ───────────────────
            Intent.ACTION_BOOT_COMPLETED -> {
                NotificationHelper.scheduleExpenseReminder(context)
            }

            // ── Alarm fired → check 3-day rule ────────────────────────────
            else -> {
                checkAndNotify(context)
            }
        }
    }

    private fun checkAndNotify(context: Context) {
        val prefs       = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastMs      = prefs.getLong(KEY_LAST_EXPENSE, 0L)
        val neverLogged = lastMs == 0L
        val overThreeDays = System.currentTimeMillis() - lastMs >= THREE_DAYS_MS

        // Only notify if never logged OR gap >= 3 days
        if (!neverLogged && !overThreeDays) return

        // Check permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        // Tap notification → open app
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("open_notifications", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat
            .Builder(context, NotificationHelper.CHANNEL_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setContentTitle("Haven't logged expenses? 🧾")
            .setContentText(
                "You haven't recorded any expenses in 3 days. " +
                        "Keep your budget tracking up to date!"
            )
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "You haven't recorded any expenses in 3 days. " +
                            "Keep your budget tracking up to date!"
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context)
                .notify(UUID.randomUUID().hashCode(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}