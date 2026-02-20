package com.tu.health.viewmodels.insights.healthconnect

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
class HealthConnectDetailsViewModel @Inject constructor(
    private val repo: InsightsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthConnectDetailsUiState())
    val uiState: StateFlow<HealthConnectDetailsUiState> = _uiState

    fun onEvent(e: HealthConnectDetailsEvent) {
        when (e) {
            HealthConnectDetailsEvent.Load -> load()
            HealthConnectDetailsEvent.Refresh -> load()

            is HealthConnectDetailsEvent.ChangeDays -> {
                _uiState.update { it.copy(selectedDays = e.days) }
                load()
            }

            HealthConnectDetailsEvent.ClearError ->
                _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun load() {
        val days = _uiState.value.selectedDays

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repo.getHealthConnect(days)

            _uiState.update { s ->
                s.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message,
                    data = result.getOrNull()
                )
            }
        }
    }
}