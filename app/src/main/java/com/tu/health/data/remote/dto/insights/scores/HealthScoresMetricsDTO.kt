package com.tu.health.data.remote.dto.insights.scores

import com.squareup.moshi.Json
import com.tu.health.data.remote.dto.insights.MetricDTO

data class HealthScoresMetricsDTO(
    @Json(name = "total")
    val total: MetricDTO,
    @Json(name = "activity")
    val activity: MetricDTO,
    @Json(name = "recovery")
    val recovery: MetricDTO,
    @Json(name = "nutrition")
    val nutrition: MetricDTO,
    @Json(name = "body_composition")
    val bodyComposition: MetricDTO
)