package com.tu.health.data.remote.dto.insights

import com.squareup.moshi.Json

data class MetricDTO(
    @Json(name = "label")
    val label: String,
    @Json(name = "unit")
    val unit: String,
    @Json(name = "description")
    val description: String
)