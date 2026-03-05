package com.tu.health.viewmodels.profile.bodyMeasurements

import com.tu.health.data.remote.dto.BodyMeasurementDTO

data class BodyMeasurementsUiState(
    val isLoading: Boolean = false,
    val measurements: List<BodyMeasurementDTO> = emptyList(),

    val weight: Float = 0f,
    val waist: Float? = 0f,
    val neck: Float? = 0f
)
