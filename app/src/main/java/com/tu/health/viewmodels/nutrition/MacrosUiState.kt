package com.tu.health.viewmodels.nutrition

import com.tu.health.data.remote.dto.MacroPlanDTO
import com.tu.health.data.remote.dto.SearchedFoodDTO
import com.tu.health.data.remote.dto.TrackedFoodDTO

data class DailyUiSummary(
    val caloriesConsumed: Float,
    val proteinConsumed: Float,
    val carbsConsumed: Float,
    val fatConsumed: Float,
    val caloriesTarget: Float?,
    val proteinTarget: Float?,
    val carbsTarget: Float?,
    val fatTarget: Float?,
)

data class MacrosUiState(
    val isLoading: Boolean = false,

    val macroPlan: MacroPlanDTO? = null,
    val trackedFoods: List<TrackedFoodDTO> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<SearchedFoodDTO> = emptyList(),

    val selectedId: Int? = null,
    val name: String = "",
    val quantity: Float = 100f,
    val unit: String = "g",
    val calories: Float = 100f,
    val protein: Float = 100f,
    val fat: Float = 100f,
    val carbs: Float = 100f,
) {
    val dailySummary: DailyUiSummary?
        get() {
            val plan = macroPlan ?: return null
            val caloriesConsumed = trackedFoods.sumOf { it.calories.toDouble() }.toFloat()
            val proteinConsumed = trackedFoods.sumOf { it.protein.toDouble() }.toFloat()
            val carbsConsumed = trackedFoods.sumOf { it.carbs.toDouble() }.toFloat()
            val fatConsumed = trackedFoods.sumOf { it.fat.toDouble() }.toFloat()

            return DailyUiSummary(
                caloriesConsumed = caloriesConsumed,
                proteinConsumed = proteinConsumed,
                carbsConsumed = carbsConsumed,
                fatConsumed = fatConsumed,
                caloriesTarget = plan.calories,
                proteinTarget = plan.proteinGrams,
                carbsTarget = plan.carbsGrams,
                fatTarget = plan.fatGrams
            )
        }
}
