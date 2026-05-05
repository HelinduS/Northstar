package com.example.northstar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incomes")
data class IncomeEntity(
    @PrimaryKey
    val id: String,
    val sourceType: String,
    val projectName: String?,
    val originalAmount: Long,
    val originalCurrency: String,
    val lkrAmount: Long,
    val exchangeRate: Double,
    val date: Long,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey
    val id: String,
    val amount: Long,
    val category: String,
    val expenseType: String,
    val paymentMethod: String,
    val description: String?,
    val date: Long,
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
    val isActive: Boolean,
    val createdAt: Long
)