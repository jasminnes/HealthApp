package com.tu.health.data.remote.dto.insights.nutrition

import com.squareup.moshi.Json

data class NutritionRangeDTO(
    val days: Int,
    @Json(name = "start_date") val startDate: String,
    @Json(name = "end_date") val endDate: String,
    val bucket: String
)
