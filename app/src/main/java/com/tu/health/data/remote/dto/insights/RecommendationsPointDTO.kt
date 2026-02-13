package com.tu.health.data.remote.dto.insights

import com.squareup.moshi.Json

data class RecommendationsPointDTO (
    @Json(name = "period_start") val periodStart: String,
    @Json(name = "new_count") val newCount: Int,
    @Json(name = "completed_count") val completedCount: Int,
    @Json(name = "dismissed_count") val dismissedCount: Int,
    @Json(name = "completion_rate") val completionRate: Double
)
