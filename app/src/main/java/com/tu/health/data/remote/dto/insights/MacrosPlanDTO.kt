package com.tu.health.data.remote.dto.insights

import com.squareup.moshi.Json

data class MacrosPlanDTO (
    val calories: Int?,
    @Json(name = "protein_g") val protein: Double?,
    @Json(name = "carbs_g") val carbs: Double?,
    @Json(name = "fat_g") val fat: Double?
)