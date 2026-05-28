package com.example.northstar.ui.notifications

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// ─────────────────────────────────────────────────────────────────────────────
// Shows precise time for trust and clarity in a finance context:
//   Today       → "9:44 AM"
//   This week   → "Mon 9:44 AM"
//   Older       → "12 Jan 9:44 AM"
// ─────────────────────────────────────────────────────────────────────────────

fun smartExactTime(timestamp: LocalDateTime): String {

    val now  = LocalDateTime.now()

    // Calculate full day difference between dates (not times)
    val days = ChronoUnit.DAYS.between(timestamp.toLocalDate(), now.toLocalDate())

    return when {

        // Today — show only the time e.g. "9:44 AM"
        days == 0L -> timestamp.format(
            DateTimeFormatter.ofPattern("h:mm a")
        )

        // This week — show day name + time e.g. "Mon 9:44 AM"
        days < 7L  -> timestamp.format(
            DateTimeFormatter.ofPattern("EEE h:mm a")
        )

        // Older — show full date + time e.g. "12 Jan 9:44 AM"
        else       -> timestamp.format(
            DateTimeFormatter.ofPattern("dd MMM h:mm a")
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// NotificationStyle Data Class
// Holds the icon, tint color, and background color for each notification type
// Used to visually differentiate notification categories in the UI
// ─────────────────────────────────────────────────────────────────────────────

data class NotificationStyle(
    val icon: ImageVector,
    val tint: Color,
    val background: Color
)

// ─────────────────────────────────────────────────────────────────────────────
// notificationStyle()
// Maps each NotificationType to its corresponding icon and colors
// Called inside NotificationCard to render the correct visual style
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun notificationStyle(type: NotificationType): NotificationStyle = when (type) {

    // ── Income notifications — green theme ───────────────────────────────────
    NotificationType.INCOME_LOGGED -> NotificationStyle(
        icon = Icons.Outlined.AccountBalanceWallet,
        tint = Color(0xFF2E7D32),
        background = Color(0xFFE8F5E9)
    )

    NotificationType.MONTHLY_GOAL_MET -> NotificationStyle(
        icon = Icons.Outlined.EmojiEvents,
        tint = Color(0xFF2E7D32),
        background = Color(0xFFE8F5E9)
    )

    // ── Expense notifications — blue theme ───────────────────────────────────
    NotificationType.EXPENSE_LOGGED -> NotificationStyle(
        icon = Icons.Outlined.Receipt,
        tint = Color(0xFF1565C0),
        background = Color(0xFFE3F2FD)
    )

    // ── Budget warnings — orange/red theme ───────────────────────────────────
    NotificationType.BUDGET_WARNING -> NotificationStyle(
        icon = Icons.Outlined.Warning,
        tint = Color(0xFFE65100),
        background = Color(0xFFFFF3E0)
    )

    NotificationType.BUDGET_CRITICAL -> NotificationStyle(
        icon = Icons.Outlined.ErrorOutline,
        tint = Color(0xFFC62828),
        background = Color(0xFFFFEBEE)
    )

    // ── Large expense — deep orange theme ────────────────────────────────────
    NotificationType.LARGE_EXPENSE -> NotificationStyle(
        icon = Icons.AutoMirrored.Outlined.TrendingDown,
        tint = Color(0xFFBF360C),
        background = Color(0xFFFBE9E7)
    )

    // ── Goal notifications — purple/indigo theme ─────────────────────────────
    NotificationType.GOAL_REACHED -> NotificationStyle(
        icon = Icons.Outlined.CheckCircle,
        tint = Color(0xFF6A1B9A),
        background = Color(0xFFF3E5F5)
    )

    NotificationType.GOAL_MILESTONE -> NotificationStyle(
        icon = Icons.Outlined.Flag,
        tint = Color(0xFF283593),
        background = Color(0xFFE8EAF6)
    )

    NotificationType.GOAL_DEADLINE -> NotificationStyle(
        icon = Icons.Outlined.Schedule,
        tint = Color(0xFFE65100),
        background = Color(0xFFFFF3E0)
    )

    // ── Reminder notifications — grey/neutral theme ──────────────────────────
    NotificationType.NO_INCOME_REMINDER -> NotificationStyle(
        icon = Icons.Outlined.NotificationsActive,
        tint = Color(0xFF37474F),
        background = Color(0xFFECEFF1)
    )

    NotificationType.NO_GOAL_PROGRESS -> NotificationStyle(
        icon = Icons.Outlined.Insights,
        tint = Color(0xFF37474F),
        background = Color(0xFFECEFF1)
    )

    // ── FR18: No expense reminder — grey/neutral theme ───────────────────────
    NotificationType.NO_EXPENSE_REMINDER -> NotificationStyle(
        icon = Icons.Outlined.NotificationsActive,
        tint = Color(0xFF37474F),
        background = Color(0xFFECEFF1)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// NotificationPanel
// The main bottom sheet panel shown when the bell icon is tapped
// Contains the header, mark-all-read button, close button, and notification list
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun NotificationPanel(
    notifications: List<NotificationItem>,
    onMarkAllRead: () -> Unit,
    onMarkRead: (String) -> Unit,
    onDelete: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.88f)
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            )
    ) {

        // ── Drag handle — visual cue that panel can be dismissed ─────────────
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .width(36.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
                .align(Alignment.CenterHorizontally)
        )

        // ── Header row — title, unread count, mark all read, close ───────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 12.dp, top = 18.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Title + unread count label
            Column {
                Text(
                    text = "Notifications",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-0.3).sp
                )

                // Only show unread count if there are unread notifications
                val unread = notifications.count { !it.isRead }
                if (unread > 0) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "$unread unread",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Action buttons — mark all read + close
            Row(verticalAlignment = Alignment.CenterVertically) {

                // Only show "Mark all read" if there are unread notifications
                if (notifications.any { !it.isRead }) {
                    TextButton(
                        onClick = onMarkAllRead,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Outlined.DoneAll,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Mark all read",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Close button — dismisses the panel
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // ── Divider between header and notification list ──────────────────────
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
            thickness = 0.8.dp
        )

        // ── Content — empty state or scrollable notification list ─────────────
        if (notifications.isEmpty()) {

            // Show empty state illustration when no notifications exist
            EmptyNotificationsView()

        } else {

            // Scrollable list of notification cards with swipe-to-delete support
            LazyColumn(
                contentPadding = PaddingValues(vertical = 6.dp, horizontal = 0.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(notifications, key = { it.id }) { notification ->

                    // Each card supports swipe left to delete
                    SwipeToDeleteNotificationCard(
                        notification = notification,
                        onMarkRead = { onMarkRead(notification.id) },
                        onDelete = { onDelete(notification.id) }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// EmptyNotificationsView
// Shown when the notification list is empty
// Displays a centred icon and friendly message
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmptyNotificationsView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Bell icon inside a circular background
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.NotificationsNone,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                )
            }

            // Primary empty state message
            Text(
                "All caught up!",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )

            // Secondary empty state message
            Text(
                "No notifications yet",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SwipeToDeleteNotificationCard
// Wraps NotificationCard with swipe-to-delete functionality
// Swipe left reveals a red delete background with trash icon
// Completing the swipe calls onDelete to remove the notification
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteNotificationCard(
    notification: NotificationItem,
    onMarkRead: () -> Unit,
    onDelete: () -> Unit
) {

    // Track the swipe dismiss state
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->

            // Only trigger delete on left swipe (EndToStart)
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,  // disable right swipe
        backgroundContent = {

            // ── Red delete background revealed on left swipe ──────────────────
            val color by animateColorAsState(
                targetValue = when (dismissState.dismissDirection) {
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFE53935)
                    else -> Color.Transparent
                },
                label = "swipe_bg"
            )

            // Scale the delete icon as the user swipes further
            val scale by animateFloatAsState(
                targetValue = if (
                    dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart
                ) 1f else 0.75f,
                label = "icon_scale"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {

                // Delete icon + label shown on swipe background
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.scale(scale)
                ) {
                    Icon(
                        Icons.Outlined.DeleteOutline,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Delete",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    ) {

        // The actual notification card sits on top of the swipe background
        NotificationCard(
            notification = notification,
            onMarkRead = onMarkRead,
            onDelete = onDelete
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// NotificationCard
// Individual notification row showing icon, title, message, and timestamp
// Tapping marks it as read (if unread)
// Unread notifications have a highlighted background and a blue dot indicator
// Timestamp uses smartExactTime() — precise and trustworthy for a finance app:
//   Today       → "9:44 AM"
//   This week   → "Mon 9:44 AM"
//   Older       → "12 Jan 9:44 AM"
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun NotificationCard(
    notification: NotificationItem,
    onMarkRead: () -> Unit,
    onDelete: () -> Unit
) {

    // Get the icon + color style for this notification type
    val style = notificationStyle(notification.type)

    // Unread notifications get a subtle highlighted background
    val bgColor = if (!notification.isRead)
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.18f)
    else
        MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable { if (!notification.isRead) onMarkRead() }  // tap to mark as read
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // ── Circular icon on the left ─────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(style.background),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = style.icon,
                contentDescription = null,
                tint = style.tint,
                modifier = Modifier.size(22.dp)
            )
        }

        // ── Title, timestamp, and message text ────────────────────────────────
        Column(modifier = Modifier.weight(1f)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Notification title — bold if unread, medium if read
                Text(
                    text = notification.title,
                    fontWeight = if (!notification.isRead) FontWeight.SemiBold else FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.width(8.dp))

                // ── Smart exact timestamp ─────────────────────────────────────
                // Today → "9:44 AM"
                // This week → "Mon 9:44 AM"
                // Older → "12 Jan 9:44 AM"
                Text(
                    text = smartExactTime(notification.timestamp),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }

            Spacer(Modifier.height(4.dp))

            // Notification message body — max 2 lines with ellipsis
            Text(
                text = notification.message,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        // ── Blue unread indicator dot on the right ────────────────────────────
        // Only shown for unread notifications
        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}