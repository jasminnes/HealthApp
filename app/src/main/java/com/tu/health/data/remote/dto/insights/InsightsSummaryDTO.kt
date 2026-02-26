package com.tu.health.data.remote.dto.insights

import com.squareup.moshi.Json
import com.tu.health.data.remote.dto.insights.bodycomposition.BodyCompositionSummaryDTO
import com.tu.health.data.remote.dto.insights.healthconnect.HealthConnectSummaryDTO
import com.tu.health.data.remote.dto.insights.nutrition.NutritionSummaryDTO
import com.tu.health.data.remote.dto.insights.scores.ScoreDTO

data class InsightsSummaryDTO(
    val range: RangeDTO,
    @Json(name = "body_composition") val bodyComposition: BodyCompositionSummaryDTO,
    val nutrition: NutritionSummaryDTO,
    @Json(name = "health_connect") val healthConnect: HealthConnectSummaryDTO,
    val scores: ScoreDTO,
)
