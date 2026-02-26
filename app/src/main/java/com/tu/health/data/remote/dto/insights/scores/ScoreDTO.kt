package com.tu.health.data.remote.dto.insights.scores

import com.squareup.moshi.Json

data class ScoreDTO(
    val total: Double?,
    val activity: Double?,
    val recovery: Double?,
    val nutrition: Double?,
    @Json(name = "body_composition") val bodyComposition: Double?
)