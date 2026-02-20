package com.tu.health.data.remote.api

import com.tu.health.data.remote.dto.insights.bodycomposition.BodyCompositionDetailsDTO
import com.tu.health.data.remote.dto.insights.InsightsSummaryDTO
import com.tu.health.data.remote.dto.insights.healthconnect.HealthConnectDetailsDTO
import com.tu.health.data.remote.dto.insights.nutrition.NutritionDetailsDTO
import com.tu.health.data.remote.dto.insights.scores.HealthScoresDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface InsightsAPI {

    @GET("insights/summary/")
    suspend fun getSummary(
        @Query("days") query: Int
    ) : InsightsSummaryDTO

    @GET("insights/nutrition/")
    suspend fun getNutrition(
        @Query("days") query: Int
    ) : NutritionDetailsDTO

    @GET("insights/body-composition/")
    suspend fun getBodyComposition(
        @Query("days") query: Int
    ) : BodyCompositionDetailsDTO

    @GET("insights/health-connect/")
    suspend fun getHealthConnect(
        @Query("days") query: Int
    ) : HealthConnectDetailsDTO

    @GET("insights/scores/")
    suspend fun getHealthScores(
        @Query("days") query: Int
    ) : HealthScoresDTO

}