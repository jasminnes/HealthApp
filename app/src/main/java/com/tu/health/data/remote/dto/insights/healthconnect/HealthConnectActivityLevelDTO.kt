package com.tu.health.data.remote.dto.insights.healthconnect

import com.squareup.moshi.Json

data class HealthConnectActivityLevelDTO(
    @Json(name = "current_activity_level") val currentActivityLevel: String?,
    @Json(name = "latest_metabolic_date") val latestMetabolicDate: String?,
    @Json(name = "last_change") val lastChange: HealthConnectActivityLevelChangeDTO?
)
