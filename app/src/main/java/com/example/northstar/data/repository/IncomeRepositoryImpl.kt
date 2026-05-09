package com.example.northstar.data.repository

import com.example.northstar.data.local.dao.IncomeDao
import com.example.northstar.data.local.entity.IncomeEntity
import com.example.northstar.data.remote.FirestoreConstants
import com.example.northstar.domain.model.Income
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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

    // --- Mapper Functions (REMOVED projectName) ---
    private fun Income.toEntity() = IncomeEntity(
        id = id,
        sourceType = sourceType,
        projectName = null, // Logic: Removed from UI, so we store null in DB
        originalAmount = originalAmount,
        originalCurrency = originalCurrency,
        lkrAmount = lkrAmount,
        exchangeRate = exchangeRate,
        date = date,
        notes = notes,
        createdAt = createdAt,
        updatedAt = System.currentTimeMillis()
    )

    private fun IncomeEntity.toDomain() = Income(
        id = id,
        sourceType = sourceType,
        projectName = null,
        originalAmount = originalAmount,
        originalCurrency = originalCurrency,
        lkrAmount = lkrAmount,
        exchangeRate = exchangeRate,
        date = date,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    // --- Create / Update / Delete ---

    override suspend fun addIncome(income: Income): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = getUserId() ?: return@withContext Result.failure(Exception("User not logged in"))
            incomeDao.insertIncome(income.toEntity())

            val firestoreData = hashMapOf(
                "sourceType" to income.sourceType,
                // projectName removed to keep Firestore clean
                "originalAmount" to income.originalAmount,
                "originalCurrency" to income.originalCurrency,
                "lkrAmount" to income.lkrAmount,
                "exchangeRate" to income.exchangeRate,
                "date" to com.google.firebase.Timestamp(java.util.Date(income.date)),
                "notes" to (income.notes ?: ""),
                "createdAt" to com.google.firebase.Timestamp.now(),
                "updatedAt" to com.google.firebase.Timestamp.now()
            )

            incomesCollection(userId).document(income.id).set(firestoreData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateIncome(income: Income): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = getUserId() ?: return@withContext Result.failure(Exception("User not logged in"))
            incomeDao.insertIncome(income.toEntity())

            val updates = mapOf(
                "sourceType" to income.sourceType,
                "originalAmount" to income.originalAmount,
                "lkrAmount" to income.lkrAmount,
                "notes" to (income.notes ?: ""),
                "updatedAt" to com.google.firebase.Timestamp.now()
            )

            incomesCollection(userId).document(income.id).update(updates).await()
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

    // --- Read (Flows) ---

    override fun getAllIncomes(): Flow<List<Income>> {
        return incomeDao.getAllIncomes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getLatestIncomes(limit: Int): Flow<List<Income>> {
        return incomeDao.getLatestIncomes(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getIncomesByDateRange(startDate: Long, endDate: Long): Flow<List<Income>> {
        return incomeDao.getIncomesByDateRange(startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getIncomesBySource(sourceType: String): Flow<List<Income>> {
        return incomeDao.getIncomesBySource(sourceType).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTotalIncomeForMonth(startTime: Long, endTime: Long): Flow<Long> {
        return incomeDao.getTotalIncomeForMonth(startTime, endTime)
    }
}