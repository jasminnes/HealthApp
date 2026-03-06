package com.tu.health.viewmodels.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.local.ProfileDataStore
import com.tu.health.data.repository.ProfileRepository
import com.tu.health.data.remote.dto.ActivityDTO
import com.tu.health.data.remote.dto.DietTypeDTO
import com.tu.health.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val profileDataStore: ProfileDataStore
) : ViewModel() {

    val firstName: StateFlow<String> = profileDataStore.profileFlow
        .map { it.firstName }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    val lastName: StateFlow<String> = profileDataStore.profileFlow
        .map { it.lastName }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    val email: StateFlow<String> = profileDataStore.profileFlow
        .map { it.email }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = Channel<ProfileUiEvent>(Channel.BUFFERED)
    val events: Flow<ProfileUiEvent> = _events.receiveAsFlow()

    val selectedDietType: StateFlow<DietTypeDTO?> =
        uiState.map { state ->
            state.allDietTypes.firstOrNull { it.id == state.selectedDietTypeId }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val selectedActivityLevel: StateFlow<ActivityDTO?> =
        uiState.map { state ->
            state.allActivityLevels.firstOrNull { it.id == state.selectedActivityLevelId }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        refreshProfile()
        loadActivityLevels()
    }

    fun onHeightChange(value: Float) {
        _uiState.update { it.copy(height = value) }
    }

    fun onWeightGoalChange(value: String) {
        _uiState.update { it.copy(weightGoal = value) }
    }

    fun onFirstNameChange(name: String) {
        _uiState.update { it.copy(firstName = name) }
    }

    fun onLastNameChange(name: String) {
        _uiState.update { it.copy(lastName = name) }
    }

    fun onDietTypeSelected(id: Int) {
        _uiState.update { it.copy(selectedDietTypeId = id) }
    }

    fun toggleCondition(conditionId: Int) {
        _uiState.update { state ->
            val newSet =
                if (state.selectedConditionIds.contains(conditionId))
                    state.selectedConditionIds - conditionId
                else
                    state.selectedConditionIds + conditionId

            state.copy(selectedConditionIds = newSet)
        }
    }

    fun refreshProfile() {
        viewModelScope.launch {
            setLoading(true)
            profileRepository.getProfile()
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            height = profile.height ?: 0f,
                            weightGoal = profile.weightGoal,
                            selectedDietTypeId = profile.dietType,
                            selectedActivityLevelId = profile.activityLevel,
                            selectedConditionIds = profile.conditions.toSet(),
                        )
                    }
                }
                .onFailure { e ->
                    emitMessage(e.localizedMessage ?: "Failed to load profile")
                }
            setLoading(false)
        }
    }

    fun loadDiets() {
        viewModelScope.launch {
            setLoading(true)
            profileRepository.getAllDietTypes()
                .onSuccess { list -> _uiState.update { it.copy(allDietTypes = list) } }
                .onFailure { e -> emitMessage(e.localizedMessage ?: "Failed to load diet types") }
            profileRepository.getRecommendedDiets()
                .onSuccess { list -> _uiState.update { it.copy(recommendedDietTypes = list) } }
                .onFailure { e -> emitMessage(e.localizedMessage ?: "Failed to load recommended diets") }
            setLoading(false)
        }
    }

    fun loadHealthConditions() {
        viewModelScope.launch {
            setLoading(true)
            profileRepository.getAllConditions()
                .onSuccess { list -> _uiState.update { it.copy(allConditions = list) } }
                .onFailure { e -> emitMessage(e.localizedMessage ?: "Failed to load conditions") }
            setLoading(false)
        }
    }

    fun loadActivityLevels() {
        viewModelScope.launch {
            setLoading(true)
            profileRepository.getAllActivityLevels()
                .onSuccess { list -> _uiState.update { it.copy(allActivityLevels = list) } }
                .onFailure { e -> emitMessage(e.localizedMessage ?: "Failed to load activity levels") }
            setLoading(false)
        }
    }

    fun updateAccount(onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            setLoading(true)
            val firstName = uiState.value.firstName.ifBlank { null }
            val lastName = uiState.value.lastName.ifBlank { null }
            authRepository.update(
                firstName = firstName,
                lastName = lastName
            )
                .onSuccess {
                    profileDataStore.saveFirstName(firstName ?: "")
                    profileDataStore.saveLastName(lastName ?: "")
                    onResult(true)
                }
                .onFailure { e ->
                    emitMessage(e.localizedMessage ?: "Failed to update user info")
                    onResult(false)
                }
        }
    }

    fun updateUserWeightGoal(onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            setLoading(true)
            val goal = uiState.value.weightGoal.trim()
            if (goal.isBlank()) {
                emitMessage("Please choose a weight goal")
                setLoading(false)
                onResult(false)
                return@launch
            }

            profileRepository.updateUserWeightGoal(goal)
                .onSuccess { onResult(true) }
                .onFailure { e ->
                    emitMessage(e.localizedMessage ?: "Failed to update weight goal")
                    onResult(false)
                }
            setLoading(false)
        }
    }

    fun updateUserHeight(onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            setLoading(true)
            val h = uiState.value.height
            if (h !in 50f..250f) {
                emitMessage("Enter a valid height (50–250 cm)")
                setLoading(false)
                onResult(false)
                return@launch
            }

            profileRepository.updateUserHeight(h)
                .onSuccess { onResult(true) }
                .onFailure { e ->
                    emitMessage(e.localizedMessage ?: "Failed to update height")
                    onResult(false)
                }
            setLoading(false)
        }
    }

    fun updateUserDietType(onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            setLoading(true)
            val id = uiState.value.selectedDietTypeId
            if (id == null) {
                emitMessage("Please select a diet type")
                setLoading(false)
                onResult(false)
                return@launch
            }

            profileRepository.updateUserDietType(id)
                .onSuccess { onResult(true) }
                .onFailure { e ->
                    emitMessage(e.localizedMessage ?: "Failed to update diet type")
                    onResult(false)
                }
            setLoading(false)
        }
    }

    fun updateUserConditions(onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            setLoading(true)
            val conditions = uiState.value.selectedConditionIds.toList()
            profileRepository.updateUserConditions(conditions)
                .onSuccess { onResult(true) }
                .onFailure { e ->
                    emitMessage(e.localizedMessage ?: "Failed to update conditions")
                    onResult(false)
                }
            setLoading(false)
        }
    }

    private fun setLoading(value: Boolean) {
        _uiState.update { it.copy(isLoading = value) }
    }

    private suspend fun emitMessage(message: String) {
        _events.send(ProfileUiEvent.ShowMessage(message))
    }
}
