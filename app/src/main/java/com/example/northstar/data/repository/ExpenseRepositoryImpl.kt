package com.example.northstar.data.repository

import com.example.northstar.data.local.dao.ExpenseDao
import com.example.northstar.data.local.entity.ExpenseEntity
import com.example.northstar.data.remote.FirestoreConstants
import com.example.northstar.domain.model.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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

    // ── Mapping helpers ───────────────────────────────────────────────────────

    private fun com.google.firebase.firestore.DocumentSnapshot.toExpense(): Expense? = try {
        Expense(
            id = id,
            amount = getLong("amount") ?: 0L,
            currency = getString("currency") ?: "LKR",
            category = getString("category") ?: "",
            expenseType = getString("expenseType") ?: "",
            paymentSource = getString("paymentMethod") ?: "",
            note = getString("description"),
            date = getTimestamp("date")?.toDate()?.time ?: 0L,
            month = getString("month") ?: "",
            createdAt = getTimestamp("createdAt")?.toDate()?.time ?: 0L,
            updatedAt = getTimestamp("updatedAt")?.toDate()?.time ?: 0L
        )
    } catch (e: Exception) { null }

    private fun Expense.toEntity() = ExpenseEntity(
        id = id,
        amount = amount,
        currency = currency,
        category = category,
        expenseType = expenseType,
        paymentSource = paymentSource,
        note = note,
        date = date,
        month = month,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun ExpenseEntity.toExpense() = Expense(
        id = id,
        amount = amount,
        currency = currency,
        category = category,
        expenseType = expenseType,
        paymentSource = paymentSource,
        note = note,
        date = date,
        month = month,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    // ── Write operations ──────────────────────────────────────────────────────

    override suspend fun addExpense(expense: Expense): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = getUserIdOrNull()
                ?: return@withContext Result.failure(IllegalStateException("User is not signed in"))
            val data = mapOf<String, Any>(
                "amount"        to expense.amount,
                "currency"      to expense.currency,
                "category"      to expense.category,
                "expenseType"   to expense.expenseType,
                "paymentMethod" to expense.paymentSource,
                "description"   to (expense.note ?: ""),
                "date"          to com.google.firebase.Timestamp(Date(expense.date)),
                "month"         to expense.month,
                "createdAt"     to com.google.firebase.Timestamp.now(),
                "updatedAt"     to com.google.firebase.Timestamp.now()
            )
            // Use the domain-model ID so Firestore and Room share the same key
            expensesCollection(userId).document(expense.id).set(data).await()
            expenseDao.insertExpense(expense.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateExpense(expense: Expense): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = getUserIdOrNull()
                ?: return@withContext Result.failure(IllegalStateException("User is not signed in"))
            val data = mapOf<String, Any>(
                "amount"        to expense.amount,
                "currency"      to expense.currency,
                "category"      to expense.category,
                "expenseType"   to expense.expenseType,
                "paymentMethod" to expense.paymentSource,
                "description"   to (expense.note ?: ""),
                "updatedAt"     to com.google.firebase.Timestamp.now()
            )
            expensesCollection(userId).document(expense.id).update(data).await()
            expenseDao.insertExpense(expense.toEntity())         // REPLACE strategy updates cache
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteExpense(expenseId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = getUserIdOrNull()
                ?: return@withContext Result.failure(IllegalStateException("User is not signed in"))
            expensesCollection(userId).document(expenseId).delete().await()
            expenseDao.deleteExpenseById(expenseId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Read operations — offline-first ───────────────────────────────────────
    // Room is the source of truth; Firestore listener keeps Room in sync when online.

    override fun getAllExpenses(): Flow<List<Expense>> = channelFlow {
        // Emit from Room immediately — works even without network
        val roomJob = launch {
            expenseDao.getAllExpenses()
                .map { entities -> entities.map { it.toExpense() } }
                .collect { trySend(it) }
        }

        // Sync Firestore → Room whenever online
        val userId = getUserIdOrNull()
        if (userId != null) {
            val listener = expensesCollection(userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    val expenses = snapshot?.documents?.mapNotNull { it.toExpense() }
                        ?: return@addSnapshotListener
                    launch(Dispatchers.IO) {
                        expenses.forEach { expenseDao.insertExpense(it.toEntity()) }
                    }
                }
            awaitClose {
                listener.remove()
                roomJob.cancel()
            }
        } else {
            awaitClose { roomJob.cancel() }
        }
    }

    override fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<Expense>> = channelFlow {
        val roomJob = launch {
            expenseDao.getExpensesByDateRange(startDate, endDate)
                .map { entities -> entities.map { it.toExpense() } }
                .collect { trySend(it) }
        }

        val userId = getUserIdOrNull()
        if (userId != null) {
            val startTs = com.google.firebase.Timestamp(Date(startDate))
            val endTs   = com.google.firebase.Timestamp(Date(endDate))
            val listener = expensesCollection(userId)
                .whereGreaterThanOrEqualTo("date", startTs)
                .whereLessThanOrEqualTo("date", endTs)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    val expenses = snapshot?.documents?.mapNotNull { it.toExpense() }
                        ?: return@addSnapshotListener
                    launch(Dispatchers.IO) {
                        expenses.forEach { expenseDao.insertExpense(it.toEntity()) }
                    }
                }
            awaitClose {
                listener.remove()
                roomJob.cancel()
            }
        } else {
            awaitClose { roomJob.cancel() }
        }
    }

    override fun getExpensesByCategory(category: String): Flow<List<Expense>> = channelFlow {
        val roomJob = launch {
            expenseDao.getExpensesByCategory(category)
                .map { entities -> entities.map { it.toExpense() } }
                .collect { trySend(it) }
        }

        val userId = getUserIdOrNull()
        if (userId != null) {
            val listener = expensesCollection(userId)
                .whereEqualTo("category", category)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    val expenses = snapshot?.documents?.mapNotNull { it.toExpense() }
                        ?: return@addSnapshotListener
                    launch(Dispatchers.IO) {
                        expenses.forEach { expenseDao.insertExpense(it.toEntity()) }
                    }
                }
            awaitClose {
                listener.remove()
                roomJob.cancel()
            }
        } else {
            awaitClose { roomJob.cancel() }
        }
    }

    override fun getExpensesByType(expenseType: String): Flow<List<Expense>> = channelFlow {
        val roomJob = launch {
            expenseDao.getExpensesByType(expenseType)
                .map { entities -> entities.map { it.toExpense() } }
                .collect { trySend(it) }
        }

        val userId = getUserIdOrNull()
        if (userId != null) {
            val listener = expensesCollection(userId)
                .whereEqualTo("expenseType", expenseType)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    val expenses = snapshot?.documents?.mapNotNull { it.toExpense() }
                        ?: return@addSnapshotListener
                    launch(Dispatchers.IO) {
                        expenses.forEach { expenseDao.insertExpense(it.toEntity()) }
                    }
                }
            awaitClose {
                listener.remove()
                roomJob.cancel()
            }
        } else {
            awaitClose { roomJob.cancel() }
        }
    }
}
