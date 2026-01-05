package com.tu.health.data.remote.request

import com.squareup.moshi.Json

data class CreateWeightGoalRequest(
    @Json(name = "name") val name: String,
    @Json(name = "goal_weight") val goalWeight: Float,
    @Json(name = "starting_weight") val startingWeight: Float,
    @Json(name = "final_date") val finalDate: String,
)

data class UpdateWeightGoalRequest(
    @Json(name = "name") val name: String,
    @Json(name = "goal_weight") val goalWeight: Float,
    @Json(name = "final_date") val finalDate: String,
)
