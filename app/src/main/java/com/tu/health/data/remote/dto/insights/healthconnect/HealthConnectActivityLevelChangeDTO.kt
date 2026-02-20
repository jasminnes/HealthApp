package com.tu.health.data.remote.dto.insights.healthconnect

import com.squareup.moshi.Json

data class HealthConnectActivityLevelChangeDTO(
    @Json(name = "from") val from: String?,
    @Json(name = "to") val to: String?,
    @Json(name = "at") val at: String?
)