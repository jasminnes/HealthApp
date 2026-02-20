package com.tu.health.data.repository

import com.tu.health.data.remote.api.InsightsAPI
import com.tu.health.data.remote.dto.insights.InsightsSummaryDTO
import com.tu.health.data.remote.dto.insights.bodycomposition.BodyCompositionDetailsDTO
import com.tu.health.data.remote.dto.insights.healthconnect.HealthConnectDetailsDTO
import com.tu.health.data.remote.dto.insights.nutrition.NutritionDetailsDTO
import com.tu.health.data.remote.dto.insights.scores.HealthScoresDTO
import javax.inject.Inject

class InsightsRepository @Inject constructor(
    private val api: InsightsAPI
) {
    suspend fun getSummary(days: Int): Result<InsightsSummaryDTO> =
        safeCall { api.getSummary(query = days) }

    suspend fun getNutrition(days: Int): Result<NutritionDetailsDTO> =
        safeCall { api.getNutrition(query = days) }

    suspend fun getScore(days: Int): Result<HealthScoresDTO> =
        safeCall { api.getHealthScores(query = days) }

    suspend fun getHealthConnect(days: Int): Result<HealthConnectDetailsDTO> =
        safeCall { api.getHealthConnect(query = days) }

    suspend fun getBodyComposition(days: Int): Result<BodyCompositionDetailsDTO> =
        safeCall { api.getBodyComposition(query = days) }
}
