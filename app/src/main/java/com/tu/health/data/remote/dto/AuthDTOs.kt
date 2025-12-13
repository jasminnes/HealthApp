package com.tu.health.data.remote.dto

import com.squareup.moshi.Json

data class RegisterRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "password2") val password2: String,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String?,
    @Json(name = "gender") val gender: String,
    @Json(name = "birth_date") val birthDate: String
)

data class RegisterResponse(
    @Json(name = "email") val email: String,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String?,
    @Json(name = "birth_date") val birthDate: String,
    @Json(name = "gender") val gender: String
)

data class LoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

data class LoginResponse(
    @Json(name = "access") val access: String,
    @Json(name = "refresh") val refresh: String,
)
