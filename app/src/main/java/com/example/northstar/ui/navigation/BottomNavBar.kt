package com.example.northstar.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.northstar.Screen
import com.example.northstar.ui.theme.InterFontFamily
import com.example.northstar.ui.theme.Navy900
import com.example.northstar.ui.theme.Surface
import com.example.northstar.ui.theme.White

sealed class BottomNavItem(val route: String, val icon: ImageVector) {
    object Home      : BottomNavItem(Screen.Dashboard.route,          Icons.Outlined.Home)
    object Analytics : BottomNavItem(Screen.Analytics.route,          Icons.AutoMirrored.Outlined.ShowChart)
    object History   : BottomNavItem(Screen.TransactionHistory.route, Icons.Outlined.DateRange)
    object Profile   : BottomNavItem(Screen.Profile.route,            Icons.Outlined.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var showAddSheet by remember { mutableStateOf(false) }

    val leftItems  = listOf(BottomNavItem.Home, BottomNavItem.Analytics)
    val rightItems = listOf(BottomNavItem.History, BottomNavItem.Profile)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(99.dp),
                    ambientColor = Color.Black.copy(alpha = 0.3f),
                    spotColor = Color.Black.copy(alpha = 0.3f)
                )
                .background(Navy900, RoundedCornerShape(99.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                leftItems.forEach { item ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        NavIconItem(
                            selected = currentRoute == item.route,
                            icon = item.icon,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(Screen.Dashboard.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .shadow(elevation = 8.dp, shape = CircleShape)
                            .background(White, CircleShape)
                            .clickable { showAddSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = "Add transaction",
                            modifier = Modifier.size(22.dp),
                            tint = Navy900
                        )
                    }
                }

                rightItems.forEach { item ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        NavIconItem(
                            selected = currentRoute == item.route,
                            icon = item.icon,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(Screen.Dashboard.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false }
        ) {
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
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = if (selected) White else White.copy(alpha = 0.3f)
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(White, CircleShape)
            )
        } else {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun AddActionSheet(
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit
) {
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = label,
            color = Navy900,
            fontWeight = FontWeight.SemiBold,
            fontFamily = InterFontFamily
        )
    }
}