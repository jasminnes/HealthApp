package com.tu.health.data.remote.dto.insights

import com.squareup.moshi.Json

data class MetabolicSummaryDTO (
    @Json(name = "latest_rmr") val latestRmr: Double?,
    @Json(name = "latest_tdee") val latestTdee: Double?,
    @Json(name = "current_activity_level") val currentActivityLevel: String?,
    @Json(name = "last_change") val lastChange: ActivityLevelChangeDTO?
)