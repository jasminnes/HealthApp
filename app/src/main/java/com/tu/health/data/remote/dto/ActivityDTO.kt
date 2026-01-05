package com.tu.health.data.remote.dto

import com.squareup.moshi.Json

data class ActivityDTO(
    @Json(name = "id")
    val id: Int,

    @Json(name = "name")
    val name: String,

    @Json(name = "description")
    val description: String,

    @Json(name = "activity_factor")
    val activityFactor: Float,

)
