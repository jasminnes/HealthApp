package com.tu.health.data.remote

import com.tu.health.data.remote.dto.request.ChangePasswordRequest
import com.tu.health.data.remote.dto.request.LoginRequest
import com.tu.health.data.remote.dto.request.LogoutRequest
import com.tu.health.data.remote.dto.request.RegisterRequest
import com.tu.health.data.remote.dto.response.DetailResponse
import com.tu.health.data.remote.dto.response.GetResponse
import com.tu.health.data.remote.dto.response.LoginResponse
import com.tu.health.data.remote.dto.response.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface AuthAPI {

    @POST("account/register/")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("/account/login/")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("account/logout/")
    suspend fun logout(
        @Header("Authorization") bearerToken: String,
        @Body request: LogoutRequest
    ): DetailResponse

    @GET("account/get/")
    suspend fun get(
        @Header("Authorization") bearerToken: String
    ): GetResponse

    @POST("account/change-password/")
    suspend fun changePassword(
        @Header("Authorization") bearerToken: String,
        @Body request: ChangePasswordRequest
    ): DetailResponse

    // @HTTP(method = "DELETE", path = "account/delete/", hasBody = false)
    @DELETE("account/delete/")
    suspend fun delete(
        @Header("Authorization") bearerToken: String,
    ): DetailResponse
}
