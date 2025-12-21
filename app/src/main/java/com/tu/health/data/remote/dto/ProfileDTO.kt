package com.tu.health.data.remote.dto

import com.squareup.moshi.Json

data class ProfileDTO(
    @Json(name = "height")
    val height: Float?,

    @Json(name = "created_date")
    val createdDate: String,

    @Json(name = "activity_level")
    val activityLevel: Int,

    @Json(name = "diet_type")
    val dietType: Int,

    @Json(name = "conditions")
    val conditions: List<Int>
)
