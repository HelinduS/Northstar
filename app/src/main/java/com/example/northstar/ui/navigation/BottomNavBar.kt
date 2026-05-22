package com.example.northstar.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.northstar.Screen
import com.example.northstar.ui.theme.InterFontFamily
import com.example.northstar.ui.theme.GreenDeep
import com.example.northstar.ui.theme.White
import androidx.compose.foundation.layout.navigationBarsPadding
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home      : BottomNavItem(Screen.Dashboard.route,          Icons.Outlined.Home,                        "Home")
    object Analytics : BottomNavItem(Screen.Analytics.route,          Icons.AutoMirrored.Outlined.ShowChart,      "Analytics")
    object History   : BottomNavItem(Screen.TransactionHistory.route, Icons.Outlined.DateRange,                   "History")
    object Profile   : BottomNavItem(Screen.Profile.route,            Icons.Outlined.Person,                      "Profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var showAddSheet by remember { mutableStateOf(false) }
    val cs = MaterialTheme.colorScheme

    val leftItems  = listOf(BottomNavItem.Home, BottomNavItem.Analytics)
    val rightItems = listOf(BottomNavItem.History, BottomNavItem.Profile)

    // Modern floating action bar design
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 12.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .background(cs.surface, RoundedCornerShape(24.dp))
            .height(72.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        leftItems.forEach { item ->
            NavIconItem(
                selected = currentRoute == item.route,
                icon = item.icon,
                label = item.label,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Centre FAB
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(elevation = 8.dp, shape = CircleShape)
                    .background(GreenDeep, CircleShape)
                    .clickable { showAddSheet = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(24.dp),
                    tint = White
                )
            }
        }

        rightItems.forEach { item ->
            NavIconItem(
                selected = currentRoute == item.route,
                icon = item.icon,
                label = item.label,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (showAddSheet) {
        ModalBottomSheet(onDismissRequest = { showAddSheet = false }) {
            AddActionSheet(
                onAddIncome = {
                    showAddSheet = false
                    navController.navigate(Screen.AddIncome.route)
                },
                onAddExpense = {
                    showAddSheet = false
                    navController.navigate(Screen.AddExpense.route)
                }
            )
        }
    }
}

@Composable
private fun NavIconItem(
    selected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val activeColor = cs.primary
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = if (selected) activeColor.copy(alpha = 0.12f) else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                tint = if (selected) activeColor else cs.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(3.dp))
        Text(
            label,
            fontSize = 9.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) activeColor else cs.onSurfaceVariant,
            fontFamily = InterFontFamily
        )
    }
}

@Composable
private fun AddActionSheet(onAddIncome: () -> Unit, onAddExpense: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        SheetOption(label = "Add Income",  onClick = onAddIncome)
        HorizontalDivider(color = Color.Black.copy(alpha = 0.06f))
        SheetOption(label = "Add Expense", onClick = onAddExpense)
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun SheetOption(label: String, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = label,
            color = cs.onSurface,
            fontWeight = FontWeight.SemiBold,
            fontFamily = InterFontFamily
        )
    }
}