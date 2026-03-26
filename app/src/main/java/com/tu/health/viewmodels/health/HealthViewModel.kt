package com.tu.health.viewmodels.health

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.repository.HealthRepository
import com.tu.health.data.repository.HealthSyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthScoreViewModel @Inject constructor(
    private val healthRepository: HealthRepository,
    private val healthSyncRepository: HealthSyncRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = Channel<HealthUiEvent>(Channel.BUFFERED)
    val events: Flow<HealthUiEvent> = _events.receiveAsFlow()

    fun loadHome() {
        viewModelScope.launch {
            setLoading(true)
            _uiState.update { it.copy(errorMessage = null) }

            val scoreResult = healthRepository.getHealthScore()
            val recsResult = healthRepository.getRecommendations()

            scoreResult
                .onSuccess { dto ->
                    _uiState.update { it.copy(score = dto.toUi()) }
                }
                .onFailure { e ->
                    val msg = e.localizedMessage ?: "Failed to load health score"
                    _uiState.update { it.copy(errorMessage = msg) }
                    emitMessage(msg)
                }

            recsResult
                .onSuccess { dto ->
                    val list = dto.toUiList()
                    _uiState.update { state ->
                        val selectedId = state.selectedRecommendation?.id
                        val updatedSelected = selectedId?.let { id -> list.firstOrNull { it.id == id } }
                        state.copy(
                            recommendations = list,
                            selectedRecommendation = updatedSelected,
                        )
                    }
                }
                .onFailure { e ->
                    val msg = e.localizedMessage ?: "Failed to load recommendations"
                    _uiState.update { it.copy(errorMessage = msg) }
                    emitMessage(msg)
                }

            setLoading(false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun syncHealthOncePerDay() {
        viewModelScope.launch {
            healthSyncRepository.syncYesterdayOncePerDay()
        }
    }

    fun selectRecommendation(id: Int) {
        val rec = uiState.value.recommendations.firstOrNull { it.id == id }
        if (rec == null) {
            viewModelScope.launch { emitMessage("Recommendation not found") }
            return
        }
        _uiState.update { it.copy(selectedRecommendation = rec)}
    }

    fun clearSelectedRecommendation() {
        _uiState.update { it.copy(selectedRecommendation = null) }
    }

    fun markRecommendationCompleted(id: Int) {
        updateRecommendationStatus(id = id, status = "completed")
    }

    fun dismissRecommendation(id: Int) {
        updateRecommendationStatus(id = id, status = "dismissed")
    }

    private fun updateRecommendationStatus(id: Int, status: String) {
        viewModelScope.launch {
            setLoading(true)
            _uiState.update { it.copy(errorMessage = null) }

            _uiState.update { state ->
                val updated = state.recommendations.map { r ->
                    if (r.id == id) r.copy(status = status.toRecommendationStatus()) else r
                }
                val selected = updated.firstOrNull { it.id == id }
                state.copy(recommendations = updated, selectedRecommendation = selected)
            }

            healthRepository.updateRecommendation(id = id, status = status)
                .onSuccess {
                    _uiState.update { state ->
                        val updated = state.recommendations.map { r ->
                            if (r.id == id) r.copy(status = status.toRecommendationStatus()) else r
                        }
                        val selected = updated.firstOrNull { it.id == id }
                        state.copy(
                            recommendations = updated,
                            selectedRecommendation = selected
                        )
                    }
                    emitMessage("Updated")
                }
                .onFailure { e ->
                    val msg = e.localizedMessage ?: "Failed to update recommendation"
                    _uiState.update { it.copy(errorMessage = msg) }
                    emitMessage(msg)
                    loadHome()
                }

            setLoading(false)
        }
    }

    private fun setLoading(value: Boolean) {
        _uiState.update { it.copy(isLoading = value) }
    }

    private suspend fun emitMessage(message: String) {
        _events.send(HealthUiEvent.ShowMessage(message))
    }
}
