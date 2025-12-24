package com.tu.health.viewmodels.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.repository.GoalsRepository
import com.tu.health.data.remote.dto.WeightGoalDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeightGoalViewModel @Inject constructor(
    private val goalsRepository: GoalsRepository
) : ViewModel() {

    private val _selectedGoalId = MutableStateFlow(0)
    val selectedGoalId: StateFlow<Int> get() = _selectedGoalId

    private val _goals = MutableStateFlow<List<WeightGoalDTO>>(emptyList())
    val goals: StateFlow<List<WeightGoalDTO>> get() = _goals

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> get() = _name

    private val _startingWeight = MutableStateFlow(70f)
    val startingWeight: StateFlow<Float> get() = _startingWeight

    private val _goalWeight = MutableStateFlow(60f)
    val goalWeight: StateFlow<Float> get() = _goalWeight

    private val _finalDate = MutableStateFlow("")
    val finalDate: StateFlow<String> get() = _finalDate

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { refreshGoals() }

    fun refreshGoals() {
        viewModelScope.launch {
            _isLoading.value = true
            _goals.value = goalsRepository.getAllWeightGoals().getOrNull().orEmpty()
            _isLoading.value = false
        }
    }

    fun onSelectedIdChange(value: Int) { _selectedGoalId.value = value }
    fun onFinalDateChange(value: String) { _finalDate.value = value }
    fun onNameChange(value: String) { _name.value = value }
    fun onStartingWeightChange(value: Float) { _startingWeight.value = value }
    fun onGoalWeightChange(value: Float) { _goalWeight.value = value }

    fun createGoalWeight(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = goalsRepository.createWeightGoal(
                name = _name.value,
                finalDate = _finalDate.value,
                goalWeight = _goalWeight.value,
                startingWeight = _startingWeight.value
            )
            result.onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.localizedMessage) }
            _isLoading.value = false
        }
    }

    fun updateGoalWeight(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = goalsRepository.updateWeightGoal(
                id = _selectedGoalId.value,
                name = _name.value,
                finalDate = _finalDate.value,
                goalWeight = _goalWeight.value
            )
            result.onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.localizedMessage) }
            _isLoading.value = false
        }
    }

    fun deleteGoalWeight(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = goalsRepository.deleteWeightGoal(id = _selectedGoalId.value)
            result.onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.localizedMessage) }
            _isLoading.value = false
        }
    }
}
