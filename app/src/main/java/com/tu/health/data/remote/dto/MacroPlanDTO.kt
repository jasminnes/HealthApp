package com.tu.health.data.remote.dto

import com.squareup.moshi.Json

data class MacroPlanDTO (
    @Json(name = "calories") val calories: Float,
    @Json(name = "protein_g") val proteinGrams: Float,
    @Json(name = "fat_g") val fatGrams: Float,
    @Json(name = "carbs_g") val carbsGrams: Float,
)
