package com.tu.health.data.remote.dto.insights.scores

import com.squareup.moshi.Json
import com.tu.health.data.remote.dto.insights.RangeDTO

data class HealthScoresDTO(
    @Json(name = "range")
    val range: RangeDTO,
    @Json(name = "summary")
    val summary: HealthScoreSummaryDTO,
    @Json(name = "points")
    val points: List<ScorePointDTO>,
    @Json(name = "metrics")
    val metrics: HealthScoresMetricsDTO
)
