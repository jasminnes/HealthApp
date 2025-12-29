package com.tu.health.viewmodels.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.remote.dto.DailyMacroSummaryDTO
import com.tu.health.data.repository.NutritionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyMacrosViewModel @Inject constructor(
    private val nutritionRepository: NutritionRepository
) : ViewModel() {

    private val _dailyMacros = MutableStateFlow<DailyMacroSummaryDTO?>(null)
    val dailyMacros: StateFlow<DailyMacroSummaryDTO?> = _dailyMacros

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init { loadData() }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = nutritionRepository.getDailyMacroSummary()

            result.onSuccess {
                _dailyMacros.value = it
            }.onFailure {
                _toastEvent.emit(it.message ?: "Error loading data")
            }
            _isLoading.value = false
        }
    }

}
