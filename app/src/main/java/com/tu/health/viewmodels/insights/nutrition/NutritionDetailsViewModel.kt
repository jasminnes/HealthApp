package com.tu.health.viewmodels.insights.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.repository.InsightsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionDetailsViewModel @Inject constructor(
    private val repo: InsightsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NutritionDetailsUiState())
    val uiState: StateFlow<NutritionDetailsUiState> = _uiState

    fun onEvent(e: NutritionDetailsEvent) {
        when (e) {
            NutritionDetailsEvent.Load -> load()
            NutritionDetailsEvent.Refresh -> load()
            is NutritionDetailsEvent.ChangeDays -> {
                _uiState.update { it.copy(selectedDays = e.days) }
                load()
            }
            NutritionDetailsEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
        }

    }

    private fun load() {
        val days = _uiState.value.selectedDays
        _uiState.update { s ->
            s.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val result = repo.getNutrition(days)
            result.fold(
                onSuccess = { dto ->
                    _uiState.update { it.copy(isLoading = false, data = dto) }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to load nutrition insights"
                        )
                    }
                }
            )
        }
    }

}
