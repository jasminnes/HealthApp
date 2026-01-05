package com.tu.health.viewmodels.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.local.ProfileDataStore
import com.tu.health.data.repository.ProfileRepository
import com.tu.health.data.remote.dto.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val profileDataStore: ProfileDataStore
) : ViewModel() {

    val firstName: StateFlow<String> = profileDataStore.profileFlow
        .map { it.firstName }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val lastName: StateFlow<String> = profileDataStore.profileFlow
        .map { it.lastName }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val email: StateFlow<String> = profileDataStore.profileFlow
        .map { it.email }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    private val _height = MutableStateFlow(0f)
    val height: StateFlow<Float> get() = _height

    private val _weightGoal = MutableStateFlow("")
    val weightGoal: StateFlow<String> get() = _weightGoal

    private val _selectedDietTypeId = MutableStateFlow<Int?>(null)
    val selectedDietTypeId: StateFlow<Int?> get() = _selectedDietTypeId

    private val _selectedActivityLevelId = MutableStateFlow<Int?>(null)

    private val _selectedConditionIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedConditionIds: StateFlow<Set<Int>> get() = _selectedConditionIds

    private val _allDietTypes = MutableStateFlow<List<DietTypeDTO>>(emptyList())
    val allDietTypes: StateFlow<List<DietTypeDTO>> get() = _allDietTypes

    private val _allActivityLevels = MutableStateFlow<List<ActivityDTO>>(emptyList())

    private val _allConditions = MutableStateFlow<List<ConditionDTO>>(emptyList())
    val allConditions: StateFlow<List<ConditionDTO>> get() = _allConditions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    val selectedDietType: StateFlow<DietTypeDTO?> = combine(
        _allDietTypes, _selectedDietTypeId
    ) { diets, selectedId ->
        diets.firstOrNull { it.id == selectedId }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val selectedActivityLevel: StateFlow<ActivityDTO?> = combine(
        _allActivityLevels, _selectedActivityLevelId
    ) { levels, selectedId ->
        levels.firstOrNull { it.id == selectedId }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun onHeightChange(value: Float) { _height.value = value }
    fun onWeightGoalChange(value: String) {_weightGoal.value = value}
    fun onDietTypeSelected(id: Int) { _selectedDietTypeId.value = id }

    fun toggleCondition(conditionId: Int) {
        _selectedConditionIds.update { current ->
            if (current.contains(conditionId)) current - conditionId else current + conditionId
        }
    }

    init {
        loadProfile()
        loadActivityLevels()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true

            profileRepository.getProfile().onSuccess { profile ->
                _height.value = profile.height!!
                _weightGoal.value = profile.weightGoal
                _selectedDietTypeId.value = profile.dietType
                _selectedActivityLevelId.value = profile.activityLevel
                _selectedConditionIds.value = profile.conditions.toSet()
            }

            _isLoading.value = false
        }
    }

    fun loadDiets() {
        viewModelScope.launch {
            _isLoading.value = true
            profileRepository.getAllDietTypes().onSuccess { _allDietTypes.value = it }
            _isLoading.value = false
        }
    }

    fun loadHealthConditions() {
        viewModelScope.launch {
            _isLoading.value = true
            profileRepository.getAllConditions().onSuccess { _allConditions.value = it }
            _isLoading.value = false
        }
    }

    fun loadActivityLevels() {
        viewModelScope.launch {
            _isLoading.value = true
            profileRepository.getAllActivityLevels().onSuccess { _allActivityLevels.value = it }
            _isLoading.value = false
        }
    }

    fun updateUserWeightGoal(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = profileRepository.updateUserWeightGoal(
                goal = _weightGoal.value
            )

            result.onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.localizedMessage) }
            _isLoading.value = false
        }
    }

    fun updateUserHeight(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = profileRepository.updateUserHeight(
                height = _height.value
            )

            result.onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.localizedMessage) }
            _isLoading.value = false
        }
    }

    fun updateUserDietType(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = profileRepository.updateUserDietType(
                dietType = _selectedDietTypeId.value
            )

            result.onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.localizedMessage) }
            _isLoading.value = false
        }
    }

    fun updateUserConditions(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = profileRepository.updateUserConditions(
                conditions = _selectedConditionIds.value.toList()
            )

            result.onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.localizedMessage) }
            _isLoading.value = false
        }
    }
}
