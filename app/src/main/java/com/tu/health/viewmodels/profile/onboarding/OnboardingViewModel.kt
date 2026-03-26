package com.tu.health.viewmodels.profile.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.repository.ProfileRepository
import com.tu.health.viewmodels.profile.ProfileUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _events = Channel<ProfileUiEvent>(Channel.BUFFERED)
    val events: Flow<ProfileUiEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            profileRepository.awaitAccessToken()
            preloadLists()
        }
    }

    private suspend fun preloadLists() {
        setLoading(true)

        val diets = profileRepository.getAllDietTypes().getOrNull().orEmpty()
        val levels = profileRepository.getAllActivityLevels().getOrNull().orEmpty()
        val conditions = profileRepository.getAllConditions().getOrNull().orEmpty()

        _uiState.update {
            it.copy(
                allDietTypes = diets,
                allActivityLevels = levels,
                allConditions = conditions
            )
        }

        setLoading(false)
    }

    fun onHeightChange(value: Float) = _uiState.update { it.copy(height = value) }
    fun onWeightChange(value: Float) = _uiState.update { it.copy(weight = value) }
    fun onWaistChange(value: Float) = _uiState.update { it.copy(waist = value) }
    fun onNeckChange(value: Float) = _uiState.update { it.copy(neck = value) }
    fun onWeightGoalChange(value: String) = _uiState.update { it.copy(weightGoal = value) }

    fun onDietTypeSelected(id: Int) = _uiState.update { it.copy(selectedDietTypeId = id) }
    fun onActivityLevelSelected(id: Int) = _uiState.update { it.copy(selectedActivityLevelId = id) }

    fun toggleCondition(id: Int) {
        _uiState.update { state ->
            val newSet =
                if (state.selectedConditionIds.contains(id)) state.selectedConditionIds - id
                else state.selectedConditionIds + id
            state.copy(selectedConditionIds = newSet)
        }
    }

    fun nextStep() {
        _uiState.update { state ->
            val next = when (state.step) {
                OnboardingStep.HEIGHT -> OnboardingStep.ACTIVITY_LEVEL
                OnboardingStep.ACTIVITY_LEVEL -> OnboardingStep.DIET_TYPE
                OnboardingStep.DIET_TYPE -> OnboardingStep.CONDITIONS
                OnboardingStep.CONDITIONS -> OnboardingStep.BODY_MEASUREMENTS
                OnboardingStep.BODY_MEASUREMENTS -> OnboardingStep.WEIGHT_GOAL
                OnboardingStep.WEIGHT_GOAL -> OnboardingStep.RECOMMENDED_DIETS
                OnboardingStep.RECOMMENDED_DIETS -> OnboardingStep.SETUP_COMPLETE
                OnboardingStep.SETUP_COMPLETE -> OnboardingStep.COMPLETE
                OnboardingStep.COMPLETE -> OnboardingStep.COMPLETE
            }
            state.copy(step = next)
        }
    }

    fun previousStep() {
        _uiState.update { state ->
            val prev = when (state.step) {
                OnboardingStep.ACTIVITY_LEVEL -> OnboardingStep.HEIGHT
                OnboardingStep.DIET_TYPE -> OnboardingStep.ACTIVITY_LEVEL
                OnboardingStep.CONDITIONS -> OnboardingStep.DIET_TYPE
                OnboardingStep.BODY_MEASUREMENTS -> OnboardingStep.CONDITIONS
                OnboardingStep.WEIGHT_GOAL -> OnboardingStep.BODY_MEASUREMENTS
                OnboardingStep.RECOMMENDED_DIETS -> OnboardingStep.WEIGHT_GOAL
                OnboardingStep.SETUP_COMPLETE -> OnboardingStep.RECOMMENDED_DIETS
                else -> state.step
            }
            state.copy(step = prev)
        }
    }


    fun onboardUser(onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            setLoading(true)

            val state = uiState.value

            if (state.height !in 50f..250f) {
                emitMessage("Enter a valid height (50–250 cm)")
                setLoading(false); onResult(false); return@launch
            }
            if (state.selectedActivityLevelId == null) {
                emitMessage("Please select activity level")
                setLoading(false); onResult(false); return@launch
            }
            if (state.selectedDietTypeId == null) {
                emitMessage("Please select diet type")
                setLoading(false); onResult(false); return@launch
            }

            profileRepository.updateProfile(
                height = state.height,
                activityLevel = state.selectedActivityLevelId,
                dietType = state.selectedDietTypeId,
                weight = state.weight,
                waist = state.waist,
                neck = state.neck,
                conditions = state.selectedConditionIds.toList(),
                weightGoal = state.weightGoal
            ).onSuccess {
                onResult(true)
            }.onFailure { e ->
                emitMessage(e.localizedMessage ?: "Onboarding failed")
                onResult(false)
            }

            setLoading(false)
        }
    }

    fun getRecommendedDiets() {
        viewModelScope.launch {
            setLoading(true)

            profileRepository.getRecommendedDiets()
                .onSuccess { diets ->
                    _uiState.update {
                        it.copy(
                            allDietTypes = diets,
                            selectedDietTypeId = null
                        )
                    }
                    _uiState.update { it.copy(step = OnboardingStep.RECOMMENDED_DIETS) }
                }
                .onFailure { e ->
                    val httpCode = (e as? HttpException)?.code()

                    if (httpCode == 404) {
                        _uiState.update { it.copy(step = OnboardingStep.SETUP_COMPLETE) }
                    } else {
                        _uiState.update { it.copy(step = OnboardingStep.COMPLETE) }
                    }
                }

            setLoading(false)
        }
    }

    fun applyRecommendedDiet(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            setLoading(true)

            val chosenId = uiState.value.selectedDietTypeId
            if (chosenId == null) {
                emitMessage("Select a diet or press Skip")
                setLoading(false)
                return@launch
            }

            profileRepository.updateUserDietType(dietType = chosenId)
                .onSuccess {
                    onDone()
                }
                .onFailure { e ->
                    emitMessage(e.localizedMessage ?: "Failed to apply diet.")
                }

            setLoading(false)
        }
    }

    fun skipRecommendedDiets() {
        nextStep()
    }

    private fun setLoading(value: Boolean) {
        _uiState.update { it.copy(isLoading = value) }
    }

    private suspend fun emitMessage(message: String) {
        _events.send(ProfileUiEvent.ShowMessage(message))
    }
}
