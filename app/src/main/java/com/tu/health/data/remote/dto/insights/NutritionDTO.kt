package com.tu.health.data.remote.dto.insights

data class NutritionDTO (
    val summary: NutritionSummaryDTO,
    val points: List<NutritionPointDTO>
)
