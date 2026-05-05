package com.example.northstar

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.northstar.ui.analytics.AnalyticsScreen
import com.example.northstar.ui.auth.AuthScreen
import com.example.northstar.ui.dashboard.DashboardScreen
import com.example.northstar.ui.expense.ExpenseScreen
import com.example.northstar.ui.goals.GoalsScreen
import com.example.northstar.ui.income.IncomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            AuthScreen(navController = navController)
        }
        composable(Screen.Register.route) {
            AuthScreen(navController = navController)
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(Screen.AddIncome.route) {
            IncomeScreen(navController = navController)
        }
        composable(Screen.AddExpense.route) {
            ExpenseScreen(navController = navController)
        }
        composable(Screen.Goals.route) {
            GoalsScreen(navController = navController)
        }
        composable(Screen.Analytics.route) {
            AnalyticsScreen(navController = navController)
        }
    }
}