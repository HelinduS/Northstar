package com.example.northstar.data.repository

import com.example.northstar.data.local.dao.GoalDao
import com.example.northstar.data.local.entity.GoalEntity
import com.example.northstar.data.remote.FirestoreConstants
import com.example.northstar.domain.model.Goal
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : GoalRepository {

    private fun getUserIdOrNull(): String? = firebaseAuth.currentUser?.uid

    private fun goalsCollection(userId: String) = firestore
        .collection(FirestoreConstants.COLLECTION_USERS)
        .document(userId)
        .collection(FirestoreConstants.COLLECTION_GOALS)

    // ── Mapping helpers ───────────────────────────────────────────────────────

    private fun com.google.firebase.firestore.DocumentSnapshot.toGoal(): Goal? = try {
        Goal(
            id = id,
            name = getString("name") ?: "",
            targetAmount = getLong("targetAmount") ?: 0L,
            savedAmount = getLong("savedAmount") ?: 0L,
            targetDate = getTimestamp("targetDate")?.toDate()?.time ?: 0L,
            currency = getString("currency") ?: "LKR",
            isActive = getBoolean("isActive") ?: true,
            createdAt = getTimestamp("createdAt")?.toDate()?.time ?: 0L
        )
    } catch (e: Exception) { null }

    private fun Goal.toEntity() = GoalEntity(
        id = id,
        name = name,
        targetAmount = targetAmount,
        savedAmount = savedAmount,
        targetDate = targetDate,
        currency = currency,
        isActive = isActive,
        createdAt = createdAt
    )

    private fun GoalEntity.toGoal() = Goal(
        id = id,
        name = name,
        targetAmount = targetAmount,
        savedAmount = savedAmount,
        targetDate = targetDate,
        currency = currency,
        isActive = isActive,
        createdAt = createdAt
    )

    // ── Write operations ──────────────────────────────────────────────────────

    override suspend fun addGoal(goal: Goal): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = getUserIdOrNull()
                ?: return@withContext Result.failure(IllegalStateException("User is not signed in"))
            val data = mapOf<String, Any>(
                "name"         to goal.name,
                "targetAmount" to goal.targetAmount,
                "savedAmount"  to goal.savedAmount,
                "targetDate"   to com.google.firebase.Timestamp(java.util.Date(goal.targetDate)),
                "currency"     to goal.currency,
                "isActive"     to goal.isActive,
                "createdAt"    to com.google.firebase.Timestamp.now()
            )
            // Use the UUID supplied by the ViewModel as both Firestore doc ID and Room PK
            goalsCollection(userId).document(goal.id).set(data).await()
            goalDao.insertGoal(goal.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateGoal(goal: Goal): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = getUserIdOrNull()
                ?: return@withContext Result.failure(IllegalStateException("User is not signed in"))
            val data = mapOf<String, Any>(
                "name"         to goal.name,
                "targetAmount" to goal.targetAmount,
                "savedAmount"  to goal.savedAmount,
                "targetDate"   to com.google.firebase.Timestamp(java.util.Date(goal.targetDate)),
                "isActive"     to goal.isActive
            )
            goalsCollection(userId).document(goal.id).update(data).await()
            goalDao.insertGoal(goal.toEntity())                 // REPLACE strategy updates cache
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteGoal(goalId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = getUserIdOrNull()
                ?: return@withContext Result.failure(IllegalStateException("User is not signed in"))
            goalsCollection(userId).document(goalId).delete().await()
            goalDao.deleteGoalById(goalId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Read operations — offline-first ───────────────────────────────────────

    override fun getAllGoals(): Flow<List<Goal>> = channelFlow {
        // Room emits immediately (works offline)
        val roomJob = launch {
            goalDao.getAllGoals()
                .map { entities -> entities.map { it.toGoal() } }
                .collect { trySend(it) }
        }

        // Firestore syncs to Room when online
        val userId = getUserIdOrNull()
        if (userId != null) {
            val listener = goalsCollection(userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    val goals = snapshot?.documents?.mapNotNull { it.toGoal() }
                        ?: return@addSnapshotListener
                    launch(Dispatchers.IO) {
                        goals.forEach { goalDao.insertGoal(it.toEntity()) }
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

    override fun getActiveGoal(): Flow<Goal?> = channelFlow {
        val roomJob = launch {
            goalDao.getActiveGoal()
                .map { it?.toGoal() }
                .collect { trySend(it) }
        }

        val userId = getUserIdOrNull()
        if (userId != null) {
            val listener = goalsCollection(userId)
                .whereEqualTo("isActive", true)
                .limit(1)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    val goal = snapshot?.documents?.firstOrNull()?.toGoal()
                    if (goal != null) {
                        launch(Dispatchers.IO) { goalDao.insertGoal(goal.toEntity()) }
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
