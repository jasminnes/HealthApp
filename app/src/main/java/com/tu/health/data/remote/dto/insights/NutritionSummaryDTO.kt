package com.tu.health.data.remote.dto.insights

import com.squareup.moshi.Json

data class NutritionSummaryDTO (
    @Json(name = "avg_calories") val avgCalories: Double?,
    @Json(name = "avg_protein_g") val avgProtein: Double?,
    @Json(name = "avg_carbs_g") val avgCarbs: Double?,
    @Json(name = "avg_fat_g") val avgFat: Double?,
    val plan: MacrosPlanDTO?
)