package com.tu.health.viewmodels.insights.scores

import com.tu.health.data.remote.dto.insights.scores.HealthScoresDTO

data class HealthScoresDetailsUiState(
    val selectedDays: Int = 90,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val data: HealthScoresDTO? = null
)