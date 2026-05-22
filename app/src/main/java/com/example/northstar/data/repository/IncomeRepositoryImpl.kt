package com.example.northstar.data.repository

import com.example.northstar.data.local.dao.IncomeDao
import com.example.northstar.data.local.entity.IncomeEntity
import com.example.northstar.data.remote.FirestoreConstants
import com.example.northstar.domain.model.Income
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncomeRepositoryImpl @Inject constructor(
    private val incomeDao: IncomeDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : IncomeRepository {

    private fun getUserId(): String? = firebaseAuth.currentUser?.uid

    private fun incomesCollection(userId: String) = firestore
        .collection(FirestoreConstants.COLLECTION_USERS)
        .document(userId)
        .collection(FirestoreConstants.COLLECTION_INCOMES)

    private val monthFmt = SimpleDateFormat("yyyy-MM", Locale.US)

    private fun com.google.firebase.firestore.DocumentSnapshot.toIncome(): Income? {
        return try {
            Income(
                id = id,
                sourceType = getString("sourceType") ?: "",
                projectName = getString("projectName"),
                amount = getLong("amount") ?: 0L,
                currency = getString("currency") ?: "LKR",
                amountLKR = getLong("amountLKR") ?: 0L,
                exchangeRate = getDouble("exchangeRate") ?: 1.0,
                receivedDate = getTimestamp("receivedDate")?.toDate()?.time ?: 0L,
                month = getString("month") ?: "",
                note = getString("note"),
                createdAt = getTimestamp("createdAt")?.toDate()?.time ?: 0L,
                updatedAt = getTimestamp("updatedAt")?.toDate()?.time ?: 0L
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun Income.toEntity() = IncomeEntity(
        id = id,
        sourceType = sourceType,
        projectName = projectName,
        amount = amount,
        currency = currency,
        amountLKR = amountLKR,
        exchangeRate = exchangeRate,
        receivedDate = receivedDate,
        month = month,
        note = note,
        createdAt = createdAt,
        updatedAt = System.currentTimeMillis()
    )

    override suspend fun addIncome(income: Income): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = getUserId() ?: return@withContext Result.failure(Exception("User not logged in"))

            val firestoreData = hashMapOf(
                "sourceType" to income.sourceType,
                "projectName" to (income.projectName ?: ""),
                "amount" to income.amount,
                "currency" to income.currency,
                "amountLKR" to income.amountLKR,
                "exchangeRate" to income.exchangeRate,
                "receivedDate" to com.google.firebase.Timestamp(Date(income.receivedDate)),
                "month" to income.month,
                "note" to (income.note ?: ""),
                "createdAt" to com.google.firebase.Timestamp.now(),
                "updatedAt" to com.google.firebase.Timestamp.now()
            )

            incomesCollection(userId).document(income.id).set(firestoreData).await()
            incomeDao.insertIncome(income.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateIncome(income: Income): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = getUserId() ?: return@withContext Result.failure(Exception("User not logged in"))

            val updates = mapOf(
                "sourceType" to income.sourceType,
                "projectName" to (income.projectName ?: ""),
                "amount" to income.amount,
                "amountLKR" to income.amountLKR,
                "note" to (income.note ?: ""),
                "updatedAt" to com.google.firebase.Timestamp.now()
            )

            incomesCollection(userId).document(income.id).update(updates).await()
            incomeDao.insertIncome(income.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteIncome(incomeId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = getUserId() ?: return@withContext Result.failure(Exception("User not logged in"))
            incomesCollection(userId).document(incomeId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAllIncomes(): Flow<List<Income>> = callbackFlow {
        val userId = getUserId() ?: return@callbackFlow
        val listener = incomesCollection(userId)
            .orderBy("receivedDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val incomes = snapshot?.documents?.mapNotNull { it.toIncome() } ?: emptyList()
                trySend(incomes)
            }
        awaitClose { listener.remove() }
    }

    override fun getIncomesByDateRange(startDate: Long, endDate: Long): Flow<List<Income>> = callbackFlow {
        val userId = getUserId() ?: return@callbackFlow

        val startTimestamp = com.google.firebase.Timestamp(Date(startDate))
        val endTimestamp = com.google.firebase.Timestamp(Date(endDate))

        val listener = incomesCollection(userId)
            .whereGreaterThanOrEqualTo("receivedDate", startTimestamp)
            .whereLessThanOrEqualTo("receivedDate", endTimestamp)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val incomes = snapshot?.documents?.mapNotNull { it.toIncome() } ?: emptyList()
                trySend(incomes)
            }
        awaitClose { listener.remove() }
    }

    override fun getLatestIncomes(limit: Int): Flow<List<Income>> = callbackFlow {
        val userId = getUserId() ?: return@callbackFlow
        val listener = incomesCollection(userId)
            .orderBy("receivedDate", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, _ ->
                val incomes = snapshot?.documents?.mapNotNull { it.toIncome() } ?: emptyList()
                trySend(incomes)
            }
        awaitClose { listener.remove() }
    }

    override fun getIncomesBySource(sourceType: String): Flow<List<Income>> = callbackFlow {
        val userId = getUserId() ?: return@callbackFlow
        val listener = incomesCollection(userId)
            .whereEqualTo("sourceType", sourceType)
            .addSnapshotListener { snapshot, _ ->
                val incomes = snapshot?.documents?.mapNotNull { it.toIncome() } ?: emptyList()
                trySend(incomes)
            }
        awaitClose { listener.remove() }
    }

    override fun getTotalIncomeForMonth(startTime: Long, endTime: Long): Flow<Long> {
        return getIncomesByDateRange(startTime, endTime).map { incomes ->
            incomes.sumOf { it.amountLKR }
        }
    }
}
