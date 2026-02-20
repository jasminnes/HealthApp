package com.tu.health.data.remote.dto.insights.bodycomposition

import com.squareup.moshi.Json

data class BodyCompositionUnitsDTO(
    @Json(name = "weight") val weight: String? = null,
    @Json(name = "waist") val waist: String? = null,
    @Json(name = "lbm") val lbm: String? = null,
    @Json(name = "bfp") val bfp: String? = null
)
