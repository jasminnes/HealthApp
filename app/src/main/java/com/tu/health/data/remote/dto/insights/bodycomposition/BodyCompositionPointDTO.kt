package com.tu.health.data.remote.dto.insights.bodycomposition

import com.squareup.moshi.Json

data class BodyCompositionPointDTO(
    @Json(name = "date") val date: String,
    @Json(name = "weight") val weight: Double? = null,
    @Json(name = "waist") val waist: Double? = null,
    @Json(name = "bfp") val bfp: Double? = null,
    @Json(name = "lbm") val lbm: Double? = null
)