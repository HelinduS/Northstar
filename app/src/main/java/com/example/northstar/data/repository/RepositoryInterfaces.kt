package com.example.northstar.data.repository

import com.example.northstar.domain.model.Expense
import com.example.northstar.domain.model.Goal
import com.example.northstar.domain.model.Income
import kotlinx.coroutines.flow.Flow

interface IncomeRepository {

    // Add a new income entry
    suspend fun addIncome(income: Income): Result<Unit>

    // Update an existing income entry
    suspend fun updateIncome(income: Income): Result<Unit>

    // Delete an income entry
    suspend fun deleteIncome(incomeId: String): Result<Unit>

    // Get all incomes as a live stream
    fun getAllIncomes(): Flow<List<Income>>

    // Get incomes filtered by date range
    fun getIncomesByDateRange(startDate: Long, endDate: Long): Flow<List<Income>>

    // Get incomes filtered by source type (SALARY, FREELANCE, etc.)
    fun getIncomesBySource(sourceType: String): Flow<List<Income>>
}

interface ExpenseRepository {

    // Add a new expense entry
    suspend fun addExpense(expense: Expense): Result<Unit>

    // Update an existing expense entry
    suspend fun updateExpense(expense: Expense): Result<Unit>

    // Delete an expense entry
    suspend fun deleteExpense(expenseId: String): Result<Unit>

    // Get all expenses as a live stream
    fun getAllExpenses(): Flow<List<Expense>>

    // Get expenses filtered by date range
    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<Expense>>

    // Get expenses filtered by category (FOOD, TRANSPORT, etc.)
    fun getExpensesByCategory(category: String): Flow<List<Expense>>

    // Get expenses filtered by type (COMMITTED or DISCRETIONARY)
    fun getExpensesByType(expenseType: String): Flow<List<Expense>>
}

interface GoalRepository {

    // Add a new savings goal
    suspend fun addGoal(goal: Goal): Result<Unit>

    // Update an existing goal
    suspend fun updateGoal(goal: Goal): Result<Unit>

    // Delete a goal
    suspend fun deleteGoal(goalId: String): Result<Unit>

    // Get all goals as a live stream
    fun getAllGoals(): Flow<List<Goal>>

    // Get the current active goal
    fun getActiveGoal(): Flow<Goal?>
}