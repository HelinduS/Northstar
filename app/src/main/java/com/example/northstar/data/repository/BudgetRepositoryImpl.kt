package com.example.northstar.data.repository

import com.example.northstar.data.local.dao.BudgetDao
import com.example.northstar.data.local.entity.BudgetEntity
import com.example.northstar.domain.model.Budget
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : BudgetRepository {

    private fun getUserIdOrNull(): String? = firebaseAuth.currentUser?.uid

    private fun budgetsCollection(userId: String) = firestore
        .collection("users")
        .document(userId)
        .collection("budgets")

    override fun getAllBudgets(): Flow<List<Budget>> = callbackFlow {
        val userId = getUserIdOrNull()
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = budgetsCollection(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val budgets = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Budget(
                            id = doc.id,
                            category = doc.getString("category") ?: doc.id,
                            limitAmount = doc.getLong("limitAmount") ?: 0L,
                            spentAmount = doc.getLong("spentAmount") ?: 0L,
                            period = doc.getString("period") ?: "CUSTOM",
                            warningThreshold = (doc.getLong("warningThreshold") ?: 80L).toInt(),
                            month = doc.getString("month") ?: "",
                            createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L,
                            startDate = doc.getLong("startDate"),
                            endDate = doc.getLong("endDate")
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(budgets)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun addBudget(budget: Budget): Result<Unit> {
        return try {
            val userId = getUserIdOrNull()
                ?: return Result.failure(IllegalStateException("User is not signed in"))

            val data = mapOf(
                "category" to budget.category,
                "limitAmount" to budget.limitAmount,
                "spentAmount" to budget.spentAmount,
                "period" to budget.period,
                "warningThreshold" to budget.warningThreshold,
                "month" to budget.month,
                "createdAt" to com.google.firebase.Timestamp(java.util.Date(budget.createdAt)),
                "startDate" to budget.startDate,
                "endDate" to budget.endDate
            )

            // Resolve a single canonical ID used across Firestore and Room.
            val resolvedId = if (budget.id.isNotEmpty()) budget.id else budget.category

            // Write to Firestore
            val docRef = budgetsCollection(userId).document(resolvedId)
            docRef.set(data).await()

            // Cache to Room
            budgetDao.insertBudget(
                BudgetEntity(
                    id = resolvedId,
                    category = budget.category,
                    limitAmount = budget.limitAmount,
                    spentAmount = budget.spentAmount,
                    period = budget.period,
                    warningThreshold = budget.warningThreshold,
                    month = budget.month,
                    createdAt = budget.createdAt,
                    startDate = budget.startDate,
                    endDate = budget.endDate
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBudget(budget: Budget): Result<Unit> {
        // Reuse addBudget (idempotent set)
        return addBudget(budget)
    }

    override suspend fun deleteBudget(category: String): Result<Unit> {
        return try {
            val userId = getUserIdOrNull()
                ?: return Result.failure(IllegalStateException("User is not signed in"))


            budgetsCollection(userId).document(category).delete().await()

            // Delete from Room (by category)
            budgetDao.deleteBudget(
                BudgetEntity(
                    id = category,
                    category = category,
                    limitAmount = 0L,
                    spentAmount = 0L,
                    period = "",
                    warningThreshold = 0,
                    month = "",
                    createdAt = 0L,
                    startDate = null,
                    endDate = null
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}