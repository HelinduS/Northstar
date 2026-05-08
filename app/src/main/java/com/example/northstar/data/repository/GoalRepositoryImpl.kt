package com.example.northstar.data.repository

import com.example.northstar.data.local.dao.GoalDao
import com.example.northstar.data.local.entity.GoalEntity
import com.example.northstar.domain.model.Goal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {

    override suspend fun addGoal(goal: Goal): Result<Unit> = runCatching {
        goalDao.insertGoal(goal.toEntity())
    }

    override suspend fun updateGoal(goal: Goal): Result<Unit> = runCatching {
        goalDao.insertGoal(goal.toEntity())
    }

    override suspend fun deleteGoal(goalId: String): Result<Unit> = runCatching {
        goalDao.deleteGoal(GoalEntity(
            id = goalId, name = "", targetAmount = 0L,
            savedAmount = 0L, targetDate = 0L,
            isActive = false, createdAt = 0L
        ))
    }

    override fun getAllGoals(): Flow<List<Goal>> =
        goalDao.getAllGoals().map { list -> list.map { it.toDomain() } }

    override fun getActiveGoal(): Flow<Goal?> =
        goalDao.getActiveGoal().map { it?.toDomain() }

    // Mappers
    private fun Goal.toEntity() = GoalEntity(
        id = id,
        name = name,
        targetAmount = targetAmount,
        savedAmount = savedAmount,
        targetDate = targetDate,
        isActive = isActive,
        createdAt = createdAt
    )

    private fun GoalEntity.toDomain() = Goal(
        id = id,
        name = name,
        targetAmount = targetAmount,
        savedAmount = savedAmount,
        targetDate = targetDate,
        isActive = isActive,
        createdAt = createdAt
    )
}