package com.tu.health.data.remote.dto

import com.squareup.moshi.Json

data class DietTypeDTO(
    val id: Int,
    val name: String,
    val description: String,

    @Json(name = "protein_ratio")
    val proteinRatio: Int,

    @Json(name = "fat_ratio")
    val fatRatio: Int,

    @Json(name = "carbs_ratio")
    val carbsRatio: Int,
)
