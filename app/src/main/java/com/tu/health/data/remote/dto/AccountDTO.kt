package com.tu.health.data.remote.dto

import com.squareup.moshi.Json

data class AccountDTO(
    @Json(name = "email") val email: String,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String?,
    @Json(name = "birth_date") val birthDate: String,
    @Json(name = "gender") val gender: String,
    @Json(name = "userprofile") val userProfile: ProfileDTO?
)
