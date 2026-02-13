package com.tu.health.data.remote.dto

import com.squareup.moshi.Json

data class RecommendationsResponseDTO (
    val id: Long?,

    @Json(name = "is_stale")
    val isStale: Boolean,

    @Json(name = "status")
    val status: String,

    @Json(name = "requested_date")
    val requestedDate: String,

    @Json(name = "recommendations")
    val recommendations: List<RecommendationsDTO?>

)