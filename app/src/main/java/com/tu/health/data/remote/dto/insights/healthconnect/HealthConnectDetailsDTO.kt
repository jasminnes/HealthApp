package com.tu.health.data.remote.dto.insights.healthconnect

import com.squareup.moshi.Json
import com.tu.health.data.remote.dto.insights.RangeDTO

data class HealthConnectDetailsDTO(
    @Json(name = "range")
    val range: RangeDTO,
    @Json(name = "summary")
    val summary: HealthConnectDetailsSummaryDTO,
    @Json(name = "points")
    val points: List<HealthConnectPointDTO>,
    @Json(name = "metrics")
    val metrics: HealthConnectMetricsDTO
)
