package com.tu.health.data.remote.request

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

data class LoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

data class ChangePasswordRequest(
    @Json(name = "old_password") val oldPassword: String,
    @Json(name = "new_password") val newPassword: String,
)

data class LogoutRequest(
    @Json(name = "refresh") val refreshToken: String,
)

data class RefreshTokenRequest(
    @Json(name = "refresh") val refreshToken: String
)
