package com.tu.health.data.remote.dto

import com.squareup.moshi.Json

data class HealthDailySnapshotDTO(
    val id: Long,
    val steps: Int,

    @Json(name = "date")
    val date: String,

    @Json(name = "hr_min_bpm")
    val hrMinBpm: Int?,

    @Json(name = "hr_avg_bpm")
    val hrAvgBpm: Int?,

    @Json(name = "hr_max_bpm")
    val hrMaxBpm: Int?,

    @Json(name = "hr_latest_bpm")
    val hrLatestBpm: Int?,

    @Json(name = "hrv_avg_rmssd_ms")
    val hrvAvgRmssdMs: Double?,

    @Json(name = "hrv_latest_rmssd_ms")
    val hrvLatestRmssdMs: Double?,

    @Json(name = "sleep_duration_min")
    val sleepDurationMin: Int?,

    @Json(name = "exercise_total_duration_min")
    val exerciseTotalDurationMin: Int,

    @Json(name = "exercise_total_active_kcal")
    val exerciseTotalActiveKcal: Double?,

    @Json(name = "exercise_sessions_count")
    val exerciseSessionsCount: Int?
)
