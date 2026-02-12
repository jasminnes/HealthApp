package com.tu.health.data.remote.dto

import com.squareup.moshi.Json

data class HealthScoreDTO(
    val id: Long?,

    @Json(name = "total")
    val total: Float?,

    @Json(name = "activity")
    val activity: Float?,

    @Json(name = "recovery")
    val recovery: Float?,

    @Json(name = "nutrition")
    val nutrition: Float?,

    @Json(name = "body_composition")
    val bodyComposition: Float?,

    @Json(name = "is_stale")
    val isStale: Boolean,

    @Json(name = "status")
    val status: String,

    @Json(name = "requested_date")
    val requestedDate: String,
)
