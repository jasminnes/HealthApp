package com.tu.health.viewmodels.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.remote.dto.TrackedFoodDTO
import com.tu.health.data.repository.NutritionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MacrosViewModel @Inject constructor(
    private val nutritionRepository: NutritionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MacrosUiState())
    val uiState: StateFlow<MacrosUiState> = _uiState.asStateFlow()

    private val _events = Channel<MacrosUiEvent>(Channel.BUFFERED)
    val events: Flow<MacrosUiEvent> = _events.receiveAsFlow()

    fun onSearchQueryChange(value: String) = _uiState.update { it.copy(searchQuery = value) }
    fun onSelectedIdChange(value: Int?) = _uiState.update { it.copy(selectedId = value) }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onCaloriesChange(value: Float) = _uiState.update { it.copy(calories = value) }
    fun onCarbsChange(value: Float) = _uiState.update { it.copy(carbs = value) }
    fun onProteinChange(value: Float) = _uiState.update { it.copy(protein = value) }
    fun onFatChange(value: Float) = _uiState.update { it.copy(fat = value) }
    fun onQuantityChange(value: Float) = _uiState.update { it.copy(quantity = value) }

    fun getMacroPlan() {
        viewModelScope.launch {
            setLoading(true)
            nutritionRepository.getMacroPlan()
                .onSuccess { plan -> _uiState.update { it.copy(macroPlan = plan) } }
                .onFailure { e -> emitMessage(e.message ?: "Error loading macro plan") }
            setLoading(false)
        }
    }

    fun search() {
        val query = uiState.value.searchQuery.trim()
        if (query.isBlank()) return

        viewModelScope.launch {
            setLoading(true)
            nutritionRepository.searchFood(query)
                .onSuccess { list -> _uiState.update { it.copy(searchResults = list) } }
                .onFailure { e -> emitMessage(e.message ?: "No search data") }
            setLoading(false)
        }
    }

    fun getTodayFood() {
        viewModelScope.launch {
            setLoading(true)
            nutritionRepository.getTodayFood()
                .onSuccess { list -> _uiState.update { it.copy(trackedFoods = list) } }
                .onFailure { e -> emitMessage(e.message ?: "No data found") }
            setLoading(false)
        }
    }

    fun createFood() {
        viewModelScope.launch {
            val s = uiState.value
            if (s.name.trim().isBlank()) {
                emitMessage("Enter food name")
                return@launch
            }
            if (s.quantity <= 0f) {
                emitMessage("Quantity must be > 0")
                return@launch
            }

            setLoading(true)
            nutritionRepository.createFood(
                name = s.name.trim(),
                quantity = s.quantity,
                calories = s.calories,
                carbs = s.carbs,
                fats = s.fat,
                protein = s.protein
            ).onSuccess {
                getTodayFood()
            }.onFailure {
                emitMessage(it.message ?: "Failed to create food")
            }
            setLoading(false)
        }
    }

    fun updateFood() {
        viewModelScope.launch {
            val s = uiState.value
            val id = s.selectedId
            if (id == null || id <= 0) {
                emitMessage("Select an item to update")
                return@launch
            }
            if (s.name.trim().isBlank()) {
                emitMessage("Enter food name")
                return@launch
            }

            setLoading(true)
            nutritionRepository.updateFood(
                id = id,
                name = s.name.trim(),
                quantity = s.quantity,
                calories = s.calories,
                carbs = s.carbs,
                fats = s.fat,
                protein = s.protein
            ).onSuccess {
                getTodayFood()
            }.onFailure {
                emitMessage(it.message ?: "Failed to update food")
            }
            setLoading(false)
        }
    }

    fun deleteFood() {
        viewModelScope.launch {
            val id = uiState.value.selectedId
            if (id == null || id <= 0) {
                emitMessage("Select an item to delete")
                return@launch
            }

            setLoading(true)
            nutritionRepository.deleteFood(id)
                .onSuccess {
                    _uiState.update { it.copy(trackedFoods = it.trackedFoods.filterNot { f -> f.id == id }) }
                    emitMessage("Item deleted")
                }
                .onFailure { emitMessage(it.message ?: "Failed to delete") }
            setLoading(false)
        }
    }

    fun loadUpdateData(t: TrackedFoodDTO) {
        _uiState.update {
            it.copy(
                selectedId = t.id,
                name = t.name,
                quantity = t.quantity,
                calories = t.calories,
                protein = t.protein,
                carbs = t.carbs,
                fat = t.fat
            )
        }
    }

    private fun setLoading(value: Boolean) = _uiState.update { it.copy(isLoading = value) }

    private suspend fun emitMessage(message: String) {
        _events.send(MacrosUiEvent.ShowMessage(message))
    }
}
