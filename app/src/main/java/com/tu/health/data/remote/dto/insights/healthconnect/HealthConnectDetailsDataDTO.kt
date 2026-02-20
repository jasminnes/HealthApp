package com.tu.health.data.remote.dto.insights.healthconnect

import com.squareup.moshi.Json

data class HealthConnectDetailsDataDTO(
    @Json(name = "steps")
    val steps: Double,
    @Json(name = "sleep_min")
    val sleepMin: Double,
    @Json(name = "exercise_min")
    val exerciseMin: Double,
    @Json(name = "active_kcal")
    val activeKcal: Double,
    @Json(name = "workouts")
    val workouts: Double
)
