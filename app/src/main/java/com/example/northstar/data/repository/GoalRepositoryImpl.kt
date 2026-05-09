package com.example.northstar.data.repository

import com.example.northstar.data.remote.FirestoreConstants
import com.example.northstar.domain.model.Goal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : GoalRepository {

    private fun getUserIdOrNull(): String? = firebaseAuth.currentUser?.uid

    private fun goalsCollection(userId: String) = firestore
        .collection(FirestoreConstants.COLLECTION_USERS)
        .document(userId)
        .collection(FirestoreConstants.COLLECTION_GOALS)

    override suspend fun addGoal(goal: Goal): Result<Unit> {
        return try {
            val userId = getUserIdOrNull()
                ?: return Result.failure(IllegalStateException("User is not signed in"))
            val data = mapOf<String, Any>(
                "name" to goal.name,
                "targetAmount" to goal.targetAmount,
                "savedAmount" to goal.savedAmount,
                "targetDate" to com.google.firebase.Timestamp(java.util.Date(goal.targetDate)),
                "isActive" to goal.isActive,
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            goalsCollection(userId).add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateGoal(goal: Goal): Result<Unit> {
        return try {
            val userId = getUserIdOrNull()
                ?: return Result.failure(IllegalStateException("User is not signed in"))
            val data = mapOf<String, Any>(
                "name" to goal.name,
                "targetAmount" to goal.targetAmount,
                "savedAmount" to goal.savedAmount,
                "targetDate" to com.google.firebase.Timestamp(java.util.Date(goal.targetDate)),
                "isActive" to goal.isActive
            )
            goalsCollection(userId)
                .document(goal.id)
                .update(data)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteGoal(goalId: String): Result<Unit> {
        return try {
            val userId = getUserIdOrNull()
                ?: return Result.failure(IllegalStateException("User is not signed in"))
            goalsCollection(userId)
                .document(goalId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAllGoals(): Flow<List<Goal>> = callbackFlow {
        val userId = getUserIdOrNull() ?: return@callbackFlow
        val listener = goalsCollection(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val goals = snapshot?.documents?.map { doc ->
                    Goal(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        targetAmount = doc.getLong("targetAmount") ?: 0L,
                        savedAmount = doc.getLong("savedAmount") ?: 0L,
                        targetDate = doc.getTimestamp("targetDate")?.toDate()?.time ?: 0L,
                        isActive = doc.getBoolean("isActive") ?: true,
                        createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L
                    )
                } ?: emptyList()
                trySend(goals)
            }
        awaitClose { listener.remove() }
    }

    override fun getActiveGoal(): Flow<Goal?> = callbackFlow {
        val userId = getUserIdOrNull() ?: return@callbackFlow
        val listener = goalsCollection(userId)
            .whereEqualTo("isActive", true)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val goal = snapshot?.documents?.firstOrNull()?.let { doc ->
                    Goal(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        targetAmount = doc.getLong("targetAmount") ?: 0L,
                        savedAmount = doc.getLong("savedAmount") ?: 0L,
                        targetDate = doc.getTimestamp("targetDate")?.toDate()?.time ?: 0L,
                        isActive = doc.getBoolean("isActive") ?: true,
                        createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L
                    )
                }
                trySend(goal)
            }
        awaitClose { listener.remove() }
    }
}