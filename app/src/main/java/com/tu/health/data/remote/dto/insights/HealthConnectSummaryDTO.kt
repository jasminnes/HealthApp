package com.tu.health.data.remote.dto.insights

import com.squareup.moshi.Json

data class HealthConnectSummaryDTO (
    @Json(name = "avg_steps") val avgSteps: Double?,
    @Json(name = "avg_sleep_min") val avgSleepMinutes: Double?,
    @Json(name = "avg_exercise_min") val avgExerciseMinutes: Double?,
    @Json(name = "avg_active_kcal") val avgActiveKcal: Double?,
    @Json(name = "avg_workouts") val avgWorkouts: Double?
)