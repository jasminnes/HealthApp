package com.tu.health.data.remote.dto.response

import com.squareup.moshi.Json
import com.tu.health.data.remote.dto.ProfileDTO

data class RegisterResponse(
    @Json(name = "email") val email: String,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String?,
    @Json(name = "birth_date") val birthDate: String,
    @Json(name = "gender") val gender: String
)

data class LoginResponse(
    @Json(name = "access") val access: String,
    @Json(name = "refresh") val refresh: String,
)

data class GetResponse(
    @Json(name = "email") val email: String,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String?,
    @Json(name = "birth_date") val birthDate: String,
    @Json(name = "gender") val gender: String,
    @Json(name = "userprofile") val userDTO: ProfileDTO
)

data class DetailResponse(
    @Json(name = "detail") val detail: String,
)

data class RefreshTokenResponse(
    @Json(name = "access") val accessToken: String,
    @Json(name = "refresh") val refreshToken: String
)
