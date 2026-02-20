package com.tu.health.data.remote.dto.insights.scores

import com.squareup.moshi.Json

data class HealthScoreSummaryDTO(
    @Json(name = "latest")
    val latest: ScorePointDTO,
    @Json(name = "avg")
    val avg: HealthScoreValuesDTO,
    @Json(name = "delta")
    val delta: HealthScoreValuesDTO,
    @Json(name = "points_count")
    val pointsCount: Int
)