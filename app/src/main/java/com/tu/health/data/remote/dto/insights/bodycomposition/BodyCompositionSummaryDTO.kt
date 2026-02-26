package com.tu.health.data.remote.dto.insights.bodycomposition

import com.squareup.moshi.Json

data class BodyCompositionSummaryDTO(
    @Json(name = "avg_weight") val avgWeight: Double?,
    @Json(name = "avg_waist") val avgWaist: Double?,
    @Json(name = "avg_bfp") val avgBfp: Double?,
    @Json(name = "avg_lbm") val avgLbm: Double?,
)