package com.tu.health.data.remote.dto.insights

import com.squareup.moshi.Json
import com.tu.health.data.remote.dto.insights.bodycomposition.BodyCompositionDTO
import com.tu.health.data.remote.dto.insights.healthconnect.HealthConnectDTO
import com.tu.health.data.remote.dto.insights.nutrition.NutritionDTO
import com.tu.health.data.remote.dto.insights.scores.ScoreDTO

data class InsightsSummaryDTO (
    val range: RangeDTO,
    @Json(name = "body_composition") val bodyComposition: BodyCompositionDTO,
    val nutrition: NutritionDTO,
    @Json(name = "health_connect") val healthConnect: HealthConnectDTO,
    val scores: ScoreDTO,
)
