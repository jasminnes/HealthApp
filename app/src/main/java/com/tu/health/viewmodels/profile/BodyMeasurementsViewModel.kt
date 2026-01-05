package com.tu.health.viewmodels.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BodyMeasurementsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BodyMeasurementsUiState())
    val uiState: StateFlow<BodyMeasurementsUiState> = _uiState.asStateFlow()

    private val _events = Channel<ProfileUiEvent>(Channel.BUFFERED)
    val events: Flow<ProfileUiEvent> = _events.receiveAsFlow()

    fun onWeightChange(value: Float) = _uiState.update { it.copy(weight = value) }
    fun onWaistChange(value: Float) = _uiState.update { it.copy(waist = value) }
    fun onNeckChange(value: Float) = _uiState.update { it.copy(neck = value) }

    fun refreshMeasurements() {
        viewModelScope.launch {
            setLoading(true)
            profileRepository.getAllBodyMeasurements()
                .onSuccess { list -> _uiState.update { it.copy(measurements = list) } }
                .onFailure { e -> emitMessage(e.localizedMessage ?: "Failed to load measurements") }
            setLoading(false)
        }
    }

    fun createBodyMeasurement(onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            setLoading(true)

            val s = uiState.value
            if (s.weight <= 0f) {
                emitMessage("Enter weight")
                setLoading(false); onDone(false); return@launch
            }

            profileRepository.createBodyMeasurement(
                weight = s.weight,
                neck = s.neck,
                waist = s.waist
            ).onSuccess {
                refreshMeasurements()
                onDone(true)
            }.onFailure { e ->
                emitMessage(e.localizedMessage ?: "Failed to create measurement")
                onDone(false)
            }

            setLoading(false)
        }
    }

    fun deleteBodyMeasurement(id: Int, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            setLoading(true)
            profileRepository.deleteBodyMeasurement(id)
                .onSuccess {
                    _uiState.update { it.copy(measurements = it.measurements.filterNot { m -> m.id == id }) }
                    onDone(true)
                }
                .onFailure { e ->
                    emitMessage(e.localizedMessage ?: "Failed to delete measurement")
                    onDone(false)
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
