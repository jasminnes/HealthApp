package com.tu.health.data.remote.dto.insights

import com.squareup.moshi.Json

data class RecommendationsSummaryDTO (
    val new: Int,
    val completed: Int,
    val dismissed: Int,
    @Json(name= "completion_rate") val completionRate: Double
)