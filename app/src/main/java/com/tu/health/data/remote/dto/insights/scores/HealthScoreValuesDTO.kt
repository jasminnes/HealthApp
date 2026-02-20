package com.tu.health.data.remote.dto.insights.scores

import com.squareup.moshi.Json

data class HealthScoreValuesDTO(
    @Json(name = "total")
    val total: Double,
    @Json(name = "activity")
    val activity: Double,
    @Json(name = "recovery")
    val recovery: Double,
    @Json(name = "nutrition")
    val nutrition: Double,
    @Json(name = "body_composition")
    val bodyComposition: Double
)
