package com.tu.health.data.remote.dto

import com.squareup.moshi.Json

data class DailyMacroSummaryDTO (
    @Json(name = "calories") val calories: String,
    @Json(name = "protein_g") val proteinGrams: String,
    @Json(name = "fat_g") val fatGrams: String,
    @Json(name = "carbs_g") val carbsGrams: String,
    @Json(name = "target_calories") val caloriesTarget: String,
    @Json(name = "target_fat_g") val fatTarget: String,
    @Json(name = "target_carbs_g") val carbsTarget: String,
    @Json(name = "target_protein_g") val proteinTarget: String,
)
