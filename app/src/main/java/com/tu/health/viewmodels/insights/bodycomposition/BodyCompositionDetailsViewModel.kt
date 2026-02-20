package com.tu.health.viewmodels.insights.bodycomposition

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
class BodyCompositionDetailsViewModel @Inject constructor(
    private val repo: InsightsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BodyCompositionDetailsUiState())
    val uiState: StateFlow<BodyCompositionDetailsUiState> = _uiState

    fun onEvent(e: BodyCompositionDetailsEvent) {
        when (e) {
            BodyCompositionDetailsEvent.Load -> load()
            BodyCompositionDetailsEvent.Refresh -> load()

            is BodyCompositionDetailsEvent.ChangeDays -> {
                _uiState.update { it.copy(selectedDays = e.days) }
                load()
            }

            BodyCompositionDetailsEvent.ClearError ->
                _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun load() {
        val days = _uiState.value.selectedDays
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val res = repo.getBodyComposition(days)
            _uiState.update { s ->
                s.copy(
                    isLoading = false,
                    errorMessage = res.exceptionOrNull()?.message,
                    data = res.getOrNull()
                )
            }
        }
    }
}