package com.example.northstar.data.repository

import com.example.northstar.data.local.dao.BudgetDao
import com.example.northstar.data.local.dao.ExpenseDao
import com.example.northstar.data.local.dao.GoalDao
import com.example.northstar.data.local.dao.IncomeDao
import com.example.northstar.data.remote.FirestoreConstants
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SyncManager — on app startup, reads every record from Room and pushes it
 * to Firestore using set() (idempotent). This guarantees that any write that
 * was saved to Room but never made it to Firestore (e.g. app killed offline)
 * is eventually synced.
 */
@Singleton
class SyncManager @Inject constructor(
    private val incomeDao: IncomeDao,
    private val expenseDao: ExpenseDao,
    private val goalDao: GoalDao,
    private val budgetDao: BudgetDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

    private fun userId(): String? = firebaseAuth.currentUser?.uid

    private fun usersCollection(userId: String) = firestore
        .collection(FirestoreConstants.COLLECTION_USERS)
        .document(userId)

    suspend fun syncRoomToFirestore() = withContext(Dispatchers.IO) {
        val userId = userId() ?: return@withContext

        // ── Incomes ──────────────────────────────────────────────────────────
        val incomes = incomeDao.getAllIncomesOnce()
        incomes.forEach { income ->
            val data = hashMapOf(
                "sourceType"       to income.sourceType,
                "projectName"      to (income.projectName ?: ""),
                "originalAmount"   to income.amount,
                "originalCurrency" to income.currency,
                "lkrAmount"        to income.amountLKR,
                "exchangeRate"     to income.exchangeRate,
                "date"             to Timestamp(Date(income.receivedDate)),
                "month"            to income.month,
                "notes"            to (income.note ?: ""),
                "createdAt"        to Timestamp(Date(income.createdAt)),
                "updatedAt"        to Timestamp(Date(income.updatedAt))
            )
            usersCollection(userId)
                .collection(FirestoreConstants.COLLECTION_INCOMES)
                .document(income.id)
                .set(data) // fire-and-forget, idempotent
        }

        // ── Expenses ─────────────────────────────────────────────────────────
        val expenses = expenseDao.getAllExpensesOnce()
        expenses.forEach { expense ->
            val data = hashMapOf(
                "amount"        to expense.amount,
                "currency"      to expense.currency,
                "category"      to expense.category,
                "expenseType"   to expense.expenseType,
                "paymentMethod" to expense.paymentSource,
                "description"   to (expense.note ?: ""),
                "date"          to Timestamp(Date(expense.date)),
                "month"         to expense.month,
                "createdAt"     to Timestamp(Date(expense.createdAt)),
                "updatedAt"     to Timestamp(Date(expense.updatedAt))
            )
            usersCollection(userId)
                .collection(FirestoreConstants.COLLECTION_EXPENSES)
                .document(expense.id)
                .set(data)
        }

        // ── Goals ─────────────────────────────────────────────────────────────
        val goals = goalDao.getAllGoalsOnce()
        goals.forEach { goal ->
            val data = hashMapOf(
                "name"         to goal.name,
                "targetAmount" to goal.targetAmount,
                "savedAmount"  to goal.savedAmount,
                "targetDate"   to Timestamp(Date(goal.targetDate)),
                "currency"     to goal.currency,
                "isActive"     to goal.isActive,
                "createdAt"    to Timestamp(Date(goal.createdAt))
            )
            usersCollection(userId)
                .collection(FirestoreConstants.COLLECTION_GOALS)
                .document(goal.id)
                .set(data)
        }

        // ── Budgets ───────────────────────────────────────────────────────────
        val budgets = budgetDao.getAllBudgetsOnce()
        budgets.forEach { budget ->
            val data = hashMapOf(
                "category"         to budget.category,
                "limitAmount"      to budget.limitAmount,
                "spentAmount"      to budget.spentAmount,
                "period"           to budget.period,
                "warningThreshold" to budget.warningThreshold,
                "month"            to budget.month,
                "createdAt"        to Timestamp(Date(budget.createdAt)),
                "startDate"        to budget.startDate,
                "endDate"          to budget.endDate
            )
            usersCollection(userId)
                .collection("budgets")
                .document(budget.id)
                .set(data)
        }
    }
}
