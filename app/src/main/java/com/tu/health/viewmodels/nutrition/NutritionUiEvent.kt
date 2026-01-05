package com.tu.health.viewmodels.nutrition

sealed interface NutritionUiEvent {
    data class ShowMessage(val message: String) : NutritionUiEvent
}
