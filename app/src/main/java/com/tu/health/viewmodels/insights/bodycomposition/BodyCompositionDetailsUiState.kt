package com.tu.health.viewmodels.insights.bodycomposition

import com.tu.health.data.remote.dto.insights.bodycomposition.BodyCompositionDetailsDTO

data class BodyCompositionDetailsUiState(
    val selectedDays: Int = 90,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val data: BodyCompositionDetailsDTO? = null
)