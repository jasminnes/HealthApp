package com.tu.health.data.remote.dto.insights.bodycomposition

import com.squareup.moshi.Json

data class BodyCompositionChartHintDTO(
    @Json(name = "units") val units: BodyCompositionUnitsDTO? = null
)
