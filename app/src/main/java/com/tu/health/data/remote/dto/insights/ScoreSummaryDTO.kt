package com.tu.health.data.remote.dto.insights

import com.squareup.moshi.Json

data class ScoreSummaryDTO (
    val latest: ScorePointDTO?,
    @Json(name = "avg_total") val avgTotal: Double?
)
