package com.tu.health.data.remote.dto

import com.squareup.moshi.Json

data class DetailDTO(
    @Json(name = "detail") val detail: String,
)