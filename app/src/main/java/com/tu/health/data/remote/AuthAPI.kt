package com.tu.health.data.remote

import com.tu.health.data.remote.dto.LoginRequest
import com.tu.health.data.remote.dto.LoginResponse
import com.tu.health.data.remote.dto.RegisterRequest
import com.tu.health.data.remote.dto.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthApi {

    @POST("account/register/")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("/account/login/")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
