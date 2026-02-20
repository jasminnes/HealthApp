package com.tu.health.data.remote.dto.insights.bodycomposition

import com.squareup.moshi.Json

data class BodyCompositionDetailsDTO(
    @Json(name = "range") val range: BodyCompositionRangeDTO,
    @Json(name = "summary") val summary: BodyCompositionDetailsSummaryDTO,
    @Json(name = "points") val points: List<BodyCompositionPointDTO>,
    @Json(name = "chart_hint") val chartHint: BodyCompositionChartHintDTO? = null
)