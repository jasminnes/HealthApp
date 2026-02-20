package com.tu.health.viewmodels.insights.healthconnect

import com.tu.health.data.remote.dto.insights.healthconnect.HealthConnectDetailsDTO

data class HealthConnectDetailsUiState(
    val selectedDays: Int = 30,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val data: HealthConnectDetailsDTO? = null
)