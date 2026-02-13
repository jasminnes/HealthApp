package com.tu.health.data.remote.dto.insights

import com.squareup.moshi.Json

data class InsightsSummaryDTO (
    val range: RangeDTO,
    @Json(name = "body_composition") val bodyComposition: BodyCompositionDTO,
    val nutrition: NutritionDTO,
    val metabolic: MetabolicDTO,
    @Json(name = "health_connect") val healthConnect: HealthConnectDTO,
    val scores: ScoreDTO,
    val recommendations: RecommendationsDTO
)
