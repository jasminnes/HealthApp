package com.tu.health.data.remote

import com.tu.health.data.remote.dto.DailyMacroSummaryDTO
import retrofit2.http.GET
import retrofit2.http.Header

interface NutritionAPI {
    @GET("/nutrition/daily-macros/")
    suspend fun getDailyMacroSummary(
        @Header("Authorization") bearerToken: String
    ): DailyMacroSummaryDTO

}