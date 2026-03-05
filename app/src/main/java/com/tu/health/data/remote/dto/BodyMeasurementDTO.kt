package com.tu.health.data.remote.dto

import com.squareup.moshi.Json

data class BodyMeasurementDTO(
    val id: Int,
    val weight: Float,
    val waist: Float?,
    val neck: Float?,

    @Json(name = "created_at")
    val createdAt: String,

    @Json(name = "user_profile")
    val userProfile: Int,
)
