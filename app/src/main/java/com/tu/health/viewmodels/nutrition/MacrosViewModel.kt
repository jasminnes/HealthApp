package com.tu.health.viewmodels.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.remote.dto.MacroPlanDTO
import com.tu.health.data.remote.dto.SearchedFoodDTO
import com.tu.health.data.remote.dto.TrackedFoodDTO
import com.tu.health.data.repository.NutritionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject


data class DailyUiSummary(
    val caloriesConsumed: Float,
    val proteinConsumed: Float,
    val carbsConsumed: Float,
    val fatConsumed: Float,
    val caloriesTarget: Float,
    val proteinTarget: Float,
    val carbsTarget: Float,
    val fatTarget: Float,
)


@HiltViewModel
class MacrosViewModel @Inject constructor(
    private val nutritionRepository: NutritionRepository
) : ViewModel() {

    private val _macroPlan = MutableStateFlow<MacroPlanDTO?>(null)

    private val _dailySummary = MutableStateFlow<DailyUiSummary?>(null)
    val dailySummary: StateFlow<DailyUiSummary?> = _dailySummary

    private val _searched = MutableStateFlow("")
    val searched: StateFlow<String> get() = _searched

    private val _results = MutableStateFlow<List<SearchedFoodDTO>>(emptyList())
    val results: StateFlow<List<SearchedFoodDTO>> = _results

    private val _trackedFoods = MutableStateFlow<List<TrackedFoodDTO>>(emptyList())
    val trackedFoods: StateFlow<List<TrackedFoodDTO>> = _trackedFoods

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> get() = _name

    private val _calories = MutableStateFlow(100f)
    val calories: StateFlow<Float> get() = _calories

    private val _protein = MutableStateFlow(100f)
    val protein: StateFlow<Float> get() = _protein

    private val _fat = MutableStateFlow(100f)
    val fat: StateFlow<Float> get() = _fat

    private val _carbs = MutableStateFlow(100f)
    val carbs: StateFlow<Float> get() = _carbs

    private val _quantity = MutableStateFlow(100f)
    val quantity: StateFlow<Float> get() = _quantity

    private val _createdDate = MutableStateFlow("")
    val createdDate: StateFlow<String> get() = _createdDate

    private val _selectedId = MutableStateFlow(0)
    val selectedId: StateFlow<Int> get() = _selectedId

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    fun onSearchedChange(value: String) { _searched.value = value }
    fun onSelectedIdChange(value: Int) { _selectedId.value = value }
    fun onSelectedNameChange(value: String) { _name.value = value }
    fun onCaloriesChange(value: Float) { _calories.value = value }
    fun onCarbsChange(value: Float) { _carbs.value = value }
    fun onProteinChange(value: Float) { _protein.value = value }
    fun onFatChange(value: Float) { _fat.value = value }
    fun onQuantityChange(value: Float) { _quantity.value = value }

    init {
        viewModelScope.launch {
            combine(_macroPlan, _trackedFoods) { plan, foods ->
                if (plan == null) return@combine null

                val caloriesConsumed = foods.sumOf { it.calories.toDouble() }.toFloat()
                val proteinConsumed = foods.sumOf { it.protein.toDouble() }.toFloat()
                val carbsConsumed = foods.sumOf { it.carbs.toDouble() }.toFloat()
                val fatConsumed = foods.sumOf { it.fat.toDouble() }.toFloat()

                DailyUiSummary(
                    caloriesConsumed = caloriesConsumed,
                    proteinConsumed = proteinConsumed,
                    carbsConsumed = carbsConsumed,
                    fatConsumed = fatConsumed,

                    caloriesTarget = plan.calories,
                    proteinTarget = plan.proteinGrams,
                    carbsTarget = plan.carbsGrams,
                    fatTarget = plan.fatGrams
                )
            }.collect { _dailySummary.value = it }
        }
    }

    fun getMacroPlan() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = nutritionRepository.getMacroPlan()

            result.onSuccess {
                _macroPlan.value = it
            }.onFailure {
                _toastEvent.emit(it.message ?: "Error loading data")
            }
            _isLoading.value = false
        }
    }

    fun search() {
        if (_searched.value.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = nutritionRepository.searchFood(_searched.value)

            result.onSuccess {
                _results.value = it
            }.onFailure {
                _toastEvent.emit(it.message ?: "No search data")
            }

            _isLoading.value = false
        }
    }

    fun getTodayFood() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = nutritionRepository.getTodayFood()

            result.onSuccess {
                _trackedFoods.value = it
            }.onFailure {
                _toastEvent.emit(it.message ?: "No data found")
            }

            _isLoading.value = false
        }
    }

    fun createFood() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = nutritionRepository.createFood(
                name = _name.value,
                calories = _calories.value,
                quantity = _quantity.value,
                fats = _fat.value,
                protein = _protein.value,
                carbs = _carbs.value
            )

            result.onSuccess {
                _calories.value = it.calories
                _protein.value = it.protein
                _fat.value = it.fat
                _carbs.value = it.carbs
            }.onFailure {
                _toastEvent.emit(it.message ?: "No data found")
            }

            _isLoading.value = false
        }
    }

    fun updateFood() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = nutritionRepository.updateFood(
                name = _name.value,
                calories = _calories.value,
                quantity = _quantity.value,
                fats = _fat.value,
                protein = _protein.value,
                carbs = _carbs.value,
                id = _selectedId.value
            )

            result.onSuccess {
                _calories.value = it.calories
                _protein.value = it.protein
                _fat.value = it.fat
                _carbs.value = it.carbs
            }.onFailure {
                _toastEvent.emit(it.message ?: "No data found")
            }

            _isLoading.value = false
        }
    }

    fun deleteFood() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = nutritionRepository.deleteFood(id = _selectedId.value)

            result.onSuccess {
                _toastEvent.emit("Item deleted")
            }.onFailure {
                _toastEvent.emit(it.message ?: "No data found")
            }

            _isLoading.value = false
        }
    }

    fun loadUpdateData(t: TrackedFoodDTO) {
        onSelectedIdChange(t.id)
        onSelectedNameChange(t.name)
        onQuantityChange(t.quantity)
        onCaloriesChange(t.calories)
        onProteinChange(t.protein)
        onCarbsChange(t.carbs)
        onFatChange(t.fat)
    }

}