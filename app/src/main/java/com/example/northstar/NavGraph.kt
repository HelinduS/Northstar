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
import com.example.northstar.ui.history.TransactionHistoryScreen
import com.example.northstar.ui.income.IncomeScreen
import com.example.northstar.ui.lock.PinLockManager
import com.example.northstar.ui.profile.ProfileScreen
import com.example.northstar.ui.settings.SettingsScreen
import com.example.northstar.ui.settings.PrivacyPolicyScreen
import com.example.northstar.ui.settings.TermsScreen


@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route,
    pinLockManager: PinLockManager
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
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
        composable(Screen.TransactionHistory.route) {
            TransactionHistoryScreen(navController = navController)
        }

        // Milestone 1 Addition: Secure Destination Node
        composable(Screen.Budgets.route) {
            com.example.northstar.ui.budget.BudgetScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                pinLockManager = pinLockManager
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(navController = navController)
        }
        composable(Screen.Terms.route) {
            TermsScreen(navController = navController)
        }
    }
}