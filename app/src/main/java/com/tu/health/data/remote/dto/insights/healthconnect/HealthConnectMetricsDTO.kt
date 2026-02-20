package com.tu.health.data.remote.dto.insights.healthconnect

import com.squareup.moshi.Json
import com.tu.health.data.remote.dto.insights.MetricDTO


data class HealthConnectMetricsDTO(
    @Json(name = "steps")
    val steps: MetricDTO,
    @Json(name = "sleep_min")
    val sleepMin: MetricDTO,
    @Json(name = "exercise_min")
    val exerciseMin: MetricDTO,
    @Json(name = "active_kcal")
    val activeKcal: MetricDTO,
    @Json(name = "workouts")
    val workouts: MetricDTO
)
