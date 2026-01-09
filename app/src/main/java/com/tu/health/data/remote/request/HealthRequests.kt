package com.tu.health.data.remote.request

import com.squareup.moshi.Json

data class HealthSnapshotRequest(
    @Json(name = "date") val date: String,
    @Json(name = "today_steps") val todaySteps: Int,
    @Json(name = "heart_rate_today") val heartRateToday: HeartRateRequest,
    @Json(name = "hrv_today") val hrvToday: HrvRequest,
    @Json(name = "latest_sleep") val latestSleep: SleepRequest,
    @Json(name = "exercise_today") val exerciseToday: ExerciseRequest,
)

data class HeartRateRequest(
    @Json(name = "min_bpm") val minBpm: Int,
    @Json(name = "avg_bpm") val avgBpm: Int,
    @Json(name = "max_bpm") val maxBpm: Int,
    @Json(name = "latest_bpm") val latestBpm: Int
)

data class HrvRequest(
    @Json(name = "avg_rmssd_ms") val avgRmssdMs: Any,
    @Json(name = "latest_rmssd_ms") val latestRmssdMs: Any,
)

data class SleepRequest(
    @Json(name = "duration_minutes") val durationMinutes: Long,
)

data class ExerciseRequest(
    @Json(name = "total_duration_minutes") val totalDurationMinutes: Long,
    @Json(name = "total_active_calories_kcal") val totalActiveCaloriesKcal: Any,
    @Json(name = "sessions") val sessionsCount: Int,
)
