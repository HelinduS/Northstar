package com.example.northstar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.northstar.data.local.dao.BudgetDao
import com.example.northstar.data.local.dao.ExpenseDao
import com.example.northstar.data.local.dao.GoalDao
import com.example.northstar.data.local.dao.IncomeDao
import com.example.northstar.data.local.entity.BudgetEntity
import com.example.northstar.data.local.entity.ExpenseEntity
import com.example.northstar.data.local.entity.GoalEntity
import com.example.northstar.data.local.entity.IncomeEntity

@Database(
    entities = [
        IncomeEntity::class,
        ExpenseEntity::class,
        GoalEntity::class,
        BudgetEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class NorthStarDatabase : RoomDatabase() {
    abstract fun incomeDao(): IncomeDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun goalDao(): GoalDao

    abstract fun budgetDao(): BudgetDao
}