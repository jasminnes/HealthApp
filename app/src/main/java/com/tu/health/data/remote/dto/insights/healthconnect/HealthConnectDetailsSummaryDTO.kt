package com.tu.health.data.remote.dto.insights.healthconnect

import com.squareup.moshi.Json

data class HealthConnectDetailsSummaryDTO(
    @Json(name = "latest")
    val latest: HealthConnectPointDTO,
    @Json(name = "avg")
    val avg: HealthConnectDetailsDataDTO,
    @Json(name = "delta")
    val delta: HealthConnectDetailsDataDTO,
    @Json(name = "points_count")
    val pointsCount: Int,

    @Json(name = "activity_level")
    val activityLevel: HealthConnectActivityLevelDTO?
)
