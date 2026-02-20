package com.tu.health.data.remote.dto.insights.scores

import com.squareup.moshi.Json
import com.tu.health.data.remote.dto.insights.scores.ScorePointDTO

data class ScoreSummaryDTO (
    val latest: ScorePointDTO?,
    @Json(name = "avg_total") val avgTotal: Double?
)