package com.tu.health.data.remote

import com.tu.health.data.remote.dto.request.CreateWeightGoalRequest
import com.tu.health.data.remote.dto.request.UpdateWeightGoalRequest
import com.tu.health.data.remote.dto.WeightGoalDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GoalsAPI {
    @POST("/goals/weight/")
    suspend fun createWeightGoal(
        @Header("Authorization") bearerToken: String,
        @Body request: CreateWeightGoalRequest
    ): WeightGoalDTO

    @GET("/goals/weight/")
    suspend fun getAllWeightGoals(
        @Header("Authorization") bearerToken: String
    ): List<WeightGoalDTO>

    @GET("/goals/weight/{id}/")
    suspend fun getWeightGoal(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
        ): WeightGoalDTO

    @PUT("/goals/weight/{id}/")
    suspend fun updateWeightGoal(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
        @Body request: UpdateWeightGoalRequest
    ): WeightGoalDTO

    @DELETE("goals/weight/{id}/")
    suspend fun deleteWeightGoal(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
    )
}
