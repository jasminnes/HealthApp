package com.tu.health.data.remote.dto.insights

import com.squareup.moshi.Json

data class RangeDTO (
    val days: Int,

    @Json(name = "start_date")
    val startDate: String,

    @Json(name = "end_date")
    val endDate: String
)