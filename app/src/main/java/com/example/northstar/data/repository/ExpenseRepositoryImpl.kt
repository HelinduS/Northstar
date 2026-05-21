package com.example.northstar.data.repository

import com.example.northstar.data.local.dao.ExpenseDao
import com.example.northstar.data.remote.FirestoreConstants
import com.example.northstar.domain.model.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ExpenseRepository {

    private fun getUserIdOrNull(): String? = firebaseAuth.currentUser?.uid

    private fun expensesCollection(userId: String) = firestore
        .collection(FirestoreConstants.COLLECTION_USERS)
        .document(userId)
        .collection(FirestoreConstants.COLLECTION_EXPENSES)

    private val monthFmt = SimpleDateFormat("yyyy-MM", Locale.US)

    override suspend fun addExpense(expense: Expense): Result<Unit> {
        return try {
            val userId = getUserIdOrNull()
                ?: return Result.failure(IllegalStateException("User is not signed in"))
            val data = mapOf<String, Any>(
                "amount" to expense.amount,
                "currency" to expense.currency,
                "category" to expense.category,
                "expenseType" to expense.expenseType,
                "paymentSource" to expense.paymentSource,
                "note" to (expense.note ?: ""),
                "date" to com.google.firebase.Timestamp(Date(expense.date)),
                "month" to expense.month,
                "createdAt" to com.google.firebase.Timestamp.now(),
                "updatedAt" to com.google.firebase.Timestamp.now()
            )
            expensesCollection(userId).add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateExpense(expense: Expense): Result<Unit> {
        return try {
            val userId = getUserIdOrNull()
                ?: return Result.failure(IllegalStateException("User is not signed in"))
            val data = mapOf<String, Any>(
                "amount" to expense.amount,
                "currency" to expense.currency,
                "category" to expense.category,
                "expenseType" to expense.expenseType,
                "paymentSource" to expense.paymentSource,
                "note" to (expense.note ?: ""),
                "updatedAt" to com.google.firebase.Timestamp.now()
            )
            expensesCollection(userId)
                .document(expense.id)
                .update(data)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteExpense(expenseId: String): Result<Unit> {
        return try {
            val userId = getUserIdOrNull()
                ?: return Result.failure(IllegalStateException("User is not signed in"))
            expensesCollection(userId)
                .document(expenseId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAllExpenses(): Flow<List<Expense>> = callbackFlow {
        val userId = getUserIdOrNull() ?: return@callbackFlow
        val listener = expensesCollection(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val expenses = snapshot?.documents?.map { doc ->
                    Expense(
                        id = doc.id,
                        amount = doc.getLong("amount") ?: 0L,
                        currency = doc.getString("currency") ?: "LKR",
                        category = doc.getString("category") ?: "",
                        expenseType = doc.getString("expenseType") ?: "",
                        paymentSource = doc.getString("paymentSource") ?: "",
                        note = doc.getString("note"),
                        date = doc.getTimestamp("date")?.toDate()?.time ?: 0L,
                        month = doc.getString("month") ?: "",
                        createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L,
                        updatedAt = doc.getTimestamp("updatedAt")?.toDate()?.time ?: 0L
                    )
                } ?: emptyList()
                trySend(expenses)
            }
        awaitClose { listener.remove() }
    }

    override fun getExpensesByDateRange(
        startDate: Long,
        endDate: Long
    ): Flow<List<Expense>> = callbackFlow {
        val userId = getUserIdOrNull() ?: return@callbackFlow
        val start = Date(startDate)
        val end = Date(endDate)
        val listener = expensesCollection(userId)
            .whereGreaterThanOrEqualTo("date", start)
            .whereLessThanOrEqualTo("date", end)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val expenses = snapshot?.documents?.map { doc ->
                    Expense(
                        id = doc.id,
                        amount = doc.getLong("amount") ?: 0L,
                        currency = doc.getString("currency") ?: "LKR",
                        category = doc.getString("category") ?: "",
                        expenseType = doc.getString("expenseType") ?: "",
                        paymentSource = doc.getString("paymentSource") ?: "",
                        note = doc.getString("note"),
                        date = doc.getTimestamp("date")?.toDate()?.time ?: 0L,
                        month = doc.getString("month") ?: "",
                        createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L,
                        updatedAt = doc.getTimestamp("updatedAt")?.toDate()?.time ?: 0L
                    )
                } ?: emptyList()
                trySend(expenses)
            }
        awaitClose { listener.remove() }
    }

    override fun getExpensesByCategory(category: String): Flow<List<Expense>> = callbackFlow {
        val userId = getUserIdOrNull() ?: return@callbackFlow
        val listener = expensesCollection(userId)
            .whereEqualTo("category", category)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val expenses = snapshot?.documents?.map { doc ->
                    Expense(
                        id = doc.id,
                        amount = doc.getLong("amount") ?: 0L,
                        currency = doc.getString("currency") ?: "LKR",
                        category = doc.getString("category") ?: "",
                        expenseType = doc.getString("expenseType") ?: "",
                        paymentSource = doc.getString("paymentSource") ?: "",
                        note = doc.getString("note"),
                        date = doc.getTimestamp("date")?.toDate()?.time ?: 0L,
                        month = doc.getString("month") ?: "",
                        createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L,
                        updatedAt = doc.getTimestamp("updatedAt")?.toDate()?.time ?: 0L
                    )
                } ?: emptyList()
                trySend(expenses)
            }
        awaitClose { listener.remove() }
    }

    override fun getExpensesByType(expenseType: String): Flow<List<Expense>> = callbackFlow {
        val userId = getUserIdOrNull() ?: return@callbackFlow
        val listener = expensesCollection(userId)
            .whereEqualTo("expenseType", expenseType)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val expenses = snapshot?.documents?.map { doc ->
                    Expense(
                        id = doc.id,
                        amount = doc.getLong("amount") ?: 0L,
                        currency = doc.getString("currency") ?: "LKR",
                        category = doc.getString("category") ?: "",
                        expenseType = doc.getString("expenseType") ?: "",
                        paymentSource = doc.getString("paymentSource") ?: "",
                        note = doc.getString("note"),
                        date = doc.getTimestamp("date")?.toDate()?.time ?: 0L,
                        month = doc.getString("month") ?: "",
                        createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L,
                        updatedAt = doc.getTimestamp("updatedAt")?.toDate()?.time ?: 0L
                    )
                } ?: emptyList()
                trySend(expenses)
            }
        awaitClose { listener.remove() }
    }
}
