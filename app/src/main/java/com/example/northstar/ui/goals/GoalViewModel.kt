package com.example.northstar.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.northstar.data.repository.GoalRepository
import com.example.northstar.domain.model.Goal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()

    private val _activeGoal = MutableStateFlow<Goal?>(null)
    val activeGoal: StateFlow<Goal?> = _activeGoal.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ── NEW: edit state ───────────────────────────────────────
    private val _editingGoal = MutableStateFlow<Goal?>(null)
    val editingGoal: StateFlow<Goal?> = _editingGoal.asStateFlow()

    fun startEditing(goal: Goal) { _editingGoal.value = goal }
    fun stopEditing()            { _editingGoal.value = null  }
    // ─────────────────────────────────────────────────────────

    init {
        loadGoals()
        loadActiveGoal()
    }

    private fun loadGoals() {
        goalRepository.getAllGoals()
            .onEach { _goals.value = it }
            .launchIn(viewModelScope)
    }

    private fun loadActiveGoal() {
        goalRepository.getActiveGoal()
            .onEach { _activeGoal.value = it }
            .launchIn(viewModelScope)
    }

    fun addGoal(name: String, targetAmount: Long, targetDate: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val goal = Goal(
                id = UUID.randomUUID().toString(),
                name = name,
                targetAmount = targetAmount,
                savedAmount = 0L,
                targetDate = targetDate,
                isActive = true,
                createdAt = System.currentTimeMillis()
            )
            goalRepository.addGoal(goal).onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun contributeToGoal(goal: Goal, amount: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val newSaved = (goal.savedAmount + amount)
                .coerceAtMost(goal.targetAmount)
            val updated = goal.copy(savedAmount = newSaved)
            goalRepository.updateGoal(updated).onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    // ── NEW: updateGoal ───────────────────────────────────────
    fun updateGoal(
        goalId: String,
        newName: String,
        newTargetAmount: Long,
        newTargetDate: Long
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val existing = _goals.value.find { it.id == goalId } ?: return@launch
            val updated = existing.copy(
                name         = newName,
                targetAmount = newTargetAmount,
                targetDate   = newTargetDate
            )
            goalRepository.updateGoal(updated).onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }
    // ─────────────────────────────────────────────────────────

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            goalRepository.deleteGoal(goalId).onFailure {
                _error.value = it.message
            }
        }
    }

    fun getProgress(goal: Goal): Float {
        if (goal.targetAmount == 0L) return 0f
        return (goal.savedAmount.toFloat() / goal.targetAmount.toFloat() * 100f)
            .coerceAtMost(100f)
    }

    fun getRemainingAmount(goal: Goal): Long {
        return (goal.targetAmount - goal.savedAmount).coerceAtLeast(0L)
    }

    fun isGoalReached(goal: Goal): Boolean {
        return goal.savedAmount >= goal.targetAmount
    }

    fun getTotalSaved(): Long = _goals.value.sumOf { it.savedAmount }
    fun getCompletedGoalsCount(): Int = _goals.value.count { isGoalReached(it) }
}