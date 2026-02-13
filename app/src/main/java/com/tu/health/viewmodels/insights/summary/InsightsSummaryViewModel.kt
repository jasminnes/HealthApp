package com.tu.health.viewmodels.insights.summary

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
class InsightsSummaryViewModel @Inject constructor(
    private val repo: InsightsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsSummaryUiState())
    val uiState: StateFlow<InsightsSummaryUiState> = _uiState

    fun onEvent(event: InsightsSummaryEvent) {
        when (event) {
            InsightsSummaryEvent.Load -> loadSummary()
            InsightsSummaryEvent.Refresh -> loadSummary()
            is InsightsSummaryEvent.ChangeDays -> {
                val days = event.days.coerceIn(7, 365)
                if (days == _uiState.value.selectedDays) return

                _uiState.update { it.copy(selectedDays = days) }
                loadSummary()
            }
            InsightsSummaryEvent.ClearError ->
                _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun loadSummary() {
        val days = _uiState.value.selectedDays

        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val result = repo.getSummary(days)

            result.fold(
                onSuccess = { dto ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            data = dto
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Something went wrong"
                        )
                    }
                }
            )
        }
    }
}
