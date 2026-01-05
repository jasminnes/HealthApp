package com.tu.health.data.remote.dto

import com.squareup.moshi.Json

data class TokensDTO(
    @Json(name = "access") val accessToken: String,
    @Json(name = "refresh") val refreshToken: String
)
