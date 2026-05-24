package com.example.northstar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incomes")
data class IncomeEntity(
    @PrimaryKey
    val id: String,
    val sourceType: String,
    val projectName: String?,
    val amount: Long,
    val currency: String,
    val amountLKR: Long,
    val exchangeRate: Double,
    val receivedDate: Long,
    val month: String,
    val note: String?,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey
    val id: String,
    val amount: Long,
    val currency: String,
    val category: String,
    val expenseType: String,
    val paymentSource: String,
    val note: String?,
    val date: Long,
    val month: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val targetAmount: Long,
    val savedAmount: Long,
    val targetDate: Long,
    val currency: String = "LKR",
    val isActive: Boolean,
    val createdAt: Long
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val id: String,                // added
    val category: String,
    val limitAmount: Long,
    val spentAmount: Long,
    val period: String,                         // added
    val warningThreshold: Int,                  // added
    val month: String,
    val createdAt: Long,
    val startDate: Long?,                       // added
    val endDate: Long?                          // added
)