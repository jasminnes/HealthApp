package com.tu.health.data.remote.request

import com.squareup.moshi.Json

data class TrackedFoodRequest(
    val name: String,
    val calories: Float,
    val unit: String,
    @Json(name = "quantity") val quantity: Float,
    @Json(name = "protein_g") val protein: Float,
    @Json(name = "carbs_g") val carbs: Float,
    @Json(name = "fat_g") val fat: Float,
)
