package com.tu.health.data.remote.dto

import com.squareup.moshi.Json

data class TrackedFoodDTO(
    val id: Int,
    val name: String,
    val calories: Float,

    @Json(name = "protein_g")
    val protein: Float,

    @Json(name = "fat_g")
    val fat: Float,

    @Json(name = "carbs_g")
    val carbs: Float,

    @Json(name = "quantity_in_grams")
    val quantity: Float,

    @Json(name = "created_at")
    val createdDate: String,
)
