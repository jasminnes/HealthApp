package com.tu.health.data.remote.api

import com.tu.health.data.remote.dto.AccountDTO
import com.tu.health.data.remote.dto.DetailDTO
import com.tu.health.data.remote.dto.TokensDTO
import com.tu.health.data.remote.request.ChangePasswordRequest
import com.tu.health.data.remote.request.LoginRequest
import com.tu.health.data.remote.request.LogoutRequest
import com.tu.health.data.remote.request.RegisterRequest
import com.tu.health.data.remote.request.UpdateAccountRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthAPI {

    @POST("account/register/")
    suspend fun register(
        @Body request: RegisterRequest
    ): AccountDTO

    @POST("account/login/")
    suspend fun login(
        @Body request: LoginRequest
    ): TokensDTO

    @POST("account/logout/")
    suspend fun logout(
        @Body request: LogoutRequest
    ): DetailDTO

    @GET("account/get/")
    suspend fun get(): AccountDTO

    @PATCH("account/update/")
    suspend fun update(
        @Body request: UpdateAccountRequest
    ) : DetailDTO

    @POST("account/change-password/")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): DetailDTO

    @DELETE("account/delete/")
    suspend fun delete(): DetailDTO
}
