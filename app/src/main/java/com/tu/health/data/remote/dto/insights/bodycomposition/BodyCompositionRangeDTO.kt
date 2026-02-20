package com.tu.health.data.remote.dto.insights.bodycomposition

import com.squareup.moshi.Json

data class BodyCompositionRangeDTO(
    @Json(name = "days") val days: Int,
    @Json(name = "start_date") val startDate: String,
    @Json(name = "end_date") val endDate: String
)
