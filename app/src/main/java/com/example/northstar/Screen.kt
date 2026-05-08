package com.example.northstar

sealed class Screen(val route: String) {

    // Auth
    object Login : Screen("login")
    object Register : Screen("register")

    // Main screens
    object Dashboard : Screen("dashboard")
    object AddIncome : Screen("add_income")
    object AddExpense : Screen("add_expense")
    object Goals : Screen("goals")
    object Analytics : Screen("analytics")
    object TransactionHistory : Screen("transaction_history")
    object Profile : Screen("profile")
}