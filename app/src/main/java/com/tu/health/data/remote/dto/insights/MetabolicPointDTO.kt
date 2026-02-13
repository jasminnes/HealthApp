package com.tu.health.data.remote.dto.insights

import com.squareup.moshi.Json

data class MetabolicPointDTO (
    val date: String,
    val rmr: Double?,
    val tdee: Double?,
    @Json(name = "activity_level") val activityLevel: String?
)
