package com.tu.health.viewmodels.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.remote.dto.BodyMeasurementDTO
import com.tu.health.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BodyMeasurementsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _selectedId = MutableStateFlow(0)
    val selectedId: StateFlow<Int> get() = _selectedId

    private val _measurements = MutableStateFlow<List<BodyMeasurementDTO>>(emptyList())
    val measurements: StateFlow<List<BodyMeasurementDTO>> get() = _measurements

    private val _weight = MutableStateFlow(0f)
    val weight: StateFlow<Float> get() = _weight

    private val _waist = MutableStateFlow(0f)
    val waist: StateFlow<Float> get() = _waist

    private val _neck = MutableStateFlow(0f)
    val neck: StateFlow<Float> get() = _neck

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun onWeightChange(value: Float) { _weight.value = value }
    fun onWaistChange(value: Float) { _waist.value = value }
    fun onNeckChange(value: Float) { _neck.value = value }

    fun getAllBodyMeasurements(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = profileRepository.getAllBodyMeasurements()
            result.onSuccess { list ->
                _measurements.value = list
                onResult(true, null)
            }.onFailure {
                onResult(false, it.localizedMessage)
            }

            _isLoading.value = false
        }
    }

    fun createBodyMeasurement(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = profileRepository.createBodyMeasurement(
                weight = _weight.value,
                neck = _neck.value,
                waist = _waist.value
            )
            result.onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.localizedMessage) }
            _isLoading.value = false
        }
    }

    fun deleteBodyMeasurement(id: Int, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = profileRepository.deleteBodyMeasurement(id = id)
            result.onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.localizedMessage) }
            _isLoading.value = false
        }
    }
}
