package com.tu.health.data.repository

import com.tu.health.data.remote.AuthApi
import com.tu.health.data.remote.dto.LoginRequest
import com.tu.health.data.remote.dto.LoginResponse
import com.tu.health.data.remote.dto.RegisterRequest
import com.tu.health.data.remote.dto.RegisterResponse
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApi
) {

    suspend fun registerUser(
        email: String,
        password: String,
        repeatPassword: String,
        firstName: String,
        lastName: String?,
        gender: String,
        birthDate: String
    ): Result<RegisterResponse> {
        return try {
            val request = RegisterRequest(
                email = email,
                password = password,
                password2 = repeatPassword,
                firstName = firstName,
                lastName = lastName,
                gender = gender,
                birthDate = birthDate
            )
            val response = api.register(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(
        email: String,
        password: String
    ): Result<LoginResponse> {
        return try {
            val request = LoginRequest(
                email = email,
                password = password
            )
            val response = api.login(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
