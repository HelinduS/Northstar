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
import java.time.format.DateTimeFormatter

// ─── Icon + Color mapping ────────────────────────────────────────────────────

data class NotificationStyle(
    val icon: ImageVector,
    val tint: Color,
    val background: Color
)

@Composable
fun notificationStyle(type: NotificationType): NotificationStyle = when (type) {
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
    NotificationType.EXPENSE_LOGGED -> NotificationStyle(
        icon = Icons.Outlined.Receipt,
        tint = Color(0xFF1565C0),
        background = Color(0xFFE3F2FD)
    )
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
    NotificationType.LARGE_EXPENSE -> NotificationStyle(
        icon = Icons.AutoMirrored.Outlined.TrendingDown,  // Fix 2
        tint = Color(0xFFBF360C),
        background = Color(0xFFFBE9E7)
    )
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
    NotificationType.NO_EXPENSE_REMINDER -> NotificationStyle(  // Fix 1
        icon = Icons.Outlined.NotificationsActive,
        tint = Color(0xFF37474F),
        background = Color(0xFFECEFF1)
    )
}

// ─── Panel ───────────────────────────────────────────────────────────────────

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
        // Drag handle
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .width(36.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
                .align(Alignment.CenterHorizontally)
        )

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 12.dp, top = 18.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Notifications",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-0.3).sp
                )
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

            Row(verticalAlignment = Alignment.CenterVertically) {
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
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
            thickness = 0.8.dp
        )

        if (notifications.isEmpty()) {
            EmptyNotificationsView()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 6.dp, horizontal = 0.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(notifications, key = { it.id }) { notification ->
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

// ─── Empty state ─────────────────────────────────────────────────────────────

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
            Text(
                "All caught up!",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
            Text(
                "No notifications yet",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
            )
        }
    }
}

// ─── Swipe-to-delete card ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteNotificationCard(
    notification: NotificationItem,
    onMarkRead: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = when (dismissState.dismissDirection) {
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFE53935)
                    else -> Color.Transparent
                },
                label = "swipe_bg"
            )
            val scale by animateFloatAsState(
                targetValue = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) 1f else 0.75f,
                label = "icon_scale"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
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
        NotificationCard(
            notification = notification,
            onMarkRead = onMarkRead,
            onDelete = onDelete
        )
    }
}

// ─── Notification card ───────────────────────────────────────────────────────

@Composable
fun NotificationCard(
    notification: NotificationItem,
    onMarkRead: () -> Unit,
    onDelete: () -> Unit
) {
    val style = notificationStyle(notification.type)

    val bgColor = if (!notification.isRead)
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.18f)
    else
        MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable { if (!notification.isRead) onMarkRead() }  // Fix 3
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Icon circle
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

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                Text(
                    text = notification.timestamp.format(DateTimeFormatter.ofPattern("h:mm a")),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = notification.message,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Unread indicator dot
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