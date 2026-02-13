package com.tu.health.data.remote.dto.insights

import com.squareup.moshi.Json

data class NutritionPointDTO (
    val date: String,
    val calories: Double,
    @Json(name = "protein_g") val protein: Double,
    @Json(name = "carbs_g") val carbs: Double,
    @Json(name = "fat_g") val fat: Double
)