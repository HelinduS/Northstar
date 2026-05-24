package com.example.northstar.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.northstar.data.local.entity.BudgetEntity
import com.example.northstar.data.local.entity.ExpenseEntity
import com.example.northstar.data.local.entity.GoalEntity
import com.example.northstar.data.local.entity.IncomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {


        @Query("SELECT * FROM incomes ORDER BY receivedDate DESC LIMIT :limit")
        fun getLatestIncomes(limit: Int): Flow<List<IncomeEntity>>

        @Query("SELECT SUM(amountLKR) FROM incomes WHERE receivedDate BETWEEN :startTime AND :endTime")
        fun getTotalIncomeForMonth(startTime: Long, endTime: Long): Flow<Long>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeEntity)

    @Delete
    suspend fun deleteIncome(income: IncomeEntity)

    @Query("SELECT * FROM incomes ORDER BY receivedDate DESC")
    fun getAllIncomes(): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE receivedDate >= :startDate AND receivedDate <= :endDate ORDER BY receivedDate DESC")
    fun getIncomesByDateRange(startDate: Long, endDate: Long): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE sourceType = :sourceType ORDER BY receivedDate DESC")
    fun getIncomesBySource(sourceType: String): Flow<List<IncomeEntity>>
}

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE expenseType = :expenseType ORDER BY date DESC")
    fun getExpensesByType(expenseType: String): Flow<List<ExpenseEntity>>

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: String)
}

@Dao
interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE isActive = 1 LIMIT 1")
    fun getActiveGoal(): Flow<GoalEntity?>

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoalById(id: String)
}

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    @Query("DELETE FROM budgets WHERE id = :budgetId")
    suspend fun deleteBudgetById(budgetId: String)

    @Query("SELECT * FROM budgets ORDER BY createdAt DESC")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE category = :category LIMIT 1")
    suspend fun findByCategory(category: String): BudgetEntity?
}