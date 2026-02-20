package com.tu.health.data.remote.dto.insights.bodycomposition

import com.squareup.moshi.Json

data class BodyCompositionDetailsSummaryDTO(
    @Json(name = "latest") val latest: BodyCompositionLatestDTO?,
    @Json(name = "delta") val delta: BodyCompositionDeltaDTO?
)
