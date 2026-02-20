package com.tu.health.data.remote.dto.insights.nutrition

data class NutritionDTO (
    val summary: NutritionSummaryDTO,
    val points: List<NutritionPointDTO>
)