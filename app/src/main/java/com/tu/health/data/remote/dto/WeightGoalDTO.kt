package com.tu.health.data.remote.dto

import com.squareup.moshi.Json

data class WeightGoalDTO(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "goal_weight") val goalWeight: Float,
    @Json(name = "starting_weight") val startingWeight: Float,
    @Json(name = "created_date") val createdDate: String,
    @Json(name = "final_date") val finalDate: String,
)