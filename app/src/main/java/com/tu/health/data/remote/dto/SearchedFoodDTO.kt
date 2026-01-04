package com.tu.health.data.remote.dto

import com.squareup.moshi.Json

data class SearchedFoodDTO(
    val name: String,
    val calories: Float,

    @Json(name = "protein_g")
    val protein: Float,

    @Json(name = "fat_g")
    val fat: Float,

    @Json(name = "carbs_g")
    val carbs: Float,
)
