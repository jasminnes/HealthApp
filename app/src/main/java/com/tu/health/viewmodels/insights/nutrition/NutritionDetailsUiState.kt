package com.tu.health.viewmodels.insights.nutrition

import com.tu.health.data.remote.dto.insights.nutrition.NutritionDetailsDTO

data class NutritionDetailsUiState(
    val selectedDays: Int = 30,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val data: NutritionDetailsDTO? = null,
    val showEnergyOverlay: Boolean = true
)