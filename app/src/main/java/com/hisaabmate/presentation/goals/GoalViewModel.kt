package com.hisaabmate.presentation.goals

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisaabmate.data.local.entity.GoalEntity
import com.hisaabmate.domain.repository.HisaabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val repository: HisaabRepository
) : ViewModel() {

    val goals: StateFlow<List<GoalEntity>> = repository.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Celebration State
    var showCelebration = mutableStateOf(false)
    
    // Add Goal Sheet State
    var showAddGoalSheet = mutableStateOf(false)

    fun addGoal(name: String, target: Double, deadline: Long, icon: String, color: String) {
        viewModelScope.launch {
            repository.insertGoal(
                GoalEntity(
                    goal_name = name,
                    target_amount = target,
                    deadline_date = deadline,
                    category_icon = icon,
                    color_hex = color
                )
            )
        }
    }

    fun topUpGoal(goal: GoalEntity, amount: Double) {
        viewModelScope.launch {
            val newSaved = goal.saved_amount + amount
            repository.updateGoal(goal.copy(saved_amount = newSaved))
            
            // Check for celebration
            if (newSaved >= goal.target_amount && goal.saved_amount < goal.target_amount) {
                showCelebration.value = true
            }
        }
    }

    fun withdrawFromGoal(goal: GoalEntity, amount: Double) {
        viewModelScope.launch {
            val newSaved = (goal.saved_amount - amount).coerceAtLeast(0.0)
            repository.updateGoal(goal.copy(saved_amount = newSaved))
        }
    }
}
