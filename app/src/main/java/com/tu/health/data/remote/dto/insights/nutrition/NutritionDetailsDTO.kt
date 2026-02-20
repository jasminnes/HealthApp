package com.tu.health.data.remote.dto.insights.nutrition

data class NutritionDetailsDTO(
    val range: NutritionRangeDTO,
    val plan: MacrosPlanDTO,
    val summary: NutritionDetailsSummaryDTO,
    val points: List<NutritionPointDTO>
)