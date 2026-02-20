package com.tu.health.data.remote.dto.insights.healthconnect

import com.squareup.moshi.Json

data class HealthConnectPointDTO (
    val date: String,
    val steps: Int?,
    @Json(name = "sleep_min") val sleepMin: Int?,
    @Json(name = "exercise_min") val exerciseMinutes: Int?,
    @Json(name = "active_kcal") val activeKcal: Double?,
    val workouts: Int?
)