package com.tu.health.data.remote.api

import com.tu.health.data.remote.dto.insights.InsightsSummaryDTO
import com.tu.health.data.remote.dto.insights.NutritionDetailsDTO
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

}