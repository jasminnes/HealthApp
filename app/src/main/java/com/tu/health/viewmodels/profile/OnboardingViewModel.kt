package com.tu.health.viewmodels.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.local.SecureTokenStore
import com.tu.health.data.remote.dto.*
import com.tu.health.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val tokenStore: SecureTokenStore,
) : ViewModel() {

    private val _step = MutableStateFlow(OnboardingStep.HEIGHT)
    val step: StateFlow<OnboardingStep> = _step

    private val _height = MutableStateFlow(170f)
    val height: StateFlow<Float> get() = _height

    private val _weight = MutableStateFlow(70f)
    val weight: StateFlow<Float> get() = _weight

    private val _waist = MutableStateFlow(75f)
    val waist: StateFlow<Float> get() = _waist

    private val _neck = MutableStateFlow(34f)
    val neck: StateFlow<Float> get() = _neck

    private val _selectedDietTypeId = MutableStateFlow<Int?>(null)
    val selectedDietTypeId: StateFlow<Int?> = _selectedDietTypeId

    private val _selectedActivityLevelId = MutableStateFlow<Int?>(null)
    val selectedActivityLevelId: StateFlow<Int?> = _selectedActivityLevelId

    private val _selectedConditionIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedConditionIds: StateFlow<Set<Int>> = _selectedConditionIds

    private val _allDietTypes = MutableStateFlow<List<DietTypeDTO>>(emptyList())
    val allDietTypes: StateFlow<List<DietTypeDTO>> = _allDietTypes

    private val _allActivityLevels = MutableStateFlow<List<ActivityDTO>>(emptyList())
    val allActivityLevels: StateFlow<List<ActivityDTO>> = _allActivityLevels

    private val _allConditions = MutableStateFlow<List<ConditionDTO>>(emptyList())
    val allConditions: StateFlow<List<ConditionDTO>> = _allConditions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            tokenStore.accessToken.first { !it.isNullOrBlank() }
            _isLoading.value = true

            _allDietTypes.value =
                profileRepository.getAllDietTypes().getOrNull().orEmpty()

            _allActivityLevels.value =
                profileRepository.getAllActivityLevels().getOrNull().orEmpty()

            _allConditions.value =
                profileRepository.getAllConditions().getOrNull().orEmpty()

            _isLoading.value = false
        }
    }

    fun onHeightChange(value: Float) { _height.value = value }
    fun onWeightChange(value: Float) { _weight.value = value }
    fun onWaistChange(value: Float) { _waist.value = value }
    fun onNeckChange(value: Float) { _neck.value = value }

    fun onDietTypeSelected(id: Int) { _selectedDietTypeId.value = id }
    fun onActivityLevelSelected(id: Int) { _selectedActivityLevelId.value = id }

    fun toggleCondition(id: Int) {
        _selectedConditionIds.update {
            if (it.contains(id)) it - id else it + id
        }
    }

    fun nextStep() {
        _step.value = when (_step.value) {
            OnboardingStep.HEIGHT -> OnboardingStep.ACTIVITY_LEVEL
            OnboardingStep.ACTIVITY_LEVEL -> OnboardingStep.DIET_TYPE
            OnboardingStep.DIET_TYPE -> OnboardingStep.CONDITIONS
            OnboardingStep.CONDITIONS -> OnboardingStep.BODY_MEASUREMENTS
            OnboardingStep.BODY_MEASUREMENTS -> OnboardingStep.COMPLETE
            OnboardingStep.COMPLETE -> OnboardingStep.COMPLETE
        }
    }

    fun previousStep() {
        _step.value = when (_step.value) {
            OnboardingStep.ACTIVITY_LEVEL -> OnboardingStep.HEIGHT
            OnboardingStep.DIET_TYPE -> OnboardingStep.ACTIVITY_LEVEL
            OnboardingStep.CONDITIONS -> OnboardingStep.DIET_TYPE
            OnboardingStep.BODY_MEASUREMENTS -> OnboardingStep.CONDITIONS
            else -> _step.value
        }
    }

    fun onboardUser(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                profileRepository.onboardUser(
                    height = _height.value,
                    activityLevel = _selectedActivityLevelId.value!!,
                    dietType = _selectedDietTypeId.value!!,
                    weight = _weight.value,
                    waist = _waist.value,
                    neck = _neck.value,
                    conditions = _selectedConditionIds.value.toList()
                )
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Onboarding failed")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun complete() {
        _step.value = OnboardingStep.COMPLETE
    }
}
