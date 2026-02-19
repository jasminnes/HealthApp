package com.tu.health.viewmodels.insights.nutrition

sealed interface NutritionDetailsEvent {
    data object Load : NutritionDetailsEvent
    data object Refresh : NutritionDetailsEvent
    data class ChangeDays(val days: Int) : NutritionDetailsEvent
    data object ClearError : NutritionDetailsEvent
}
