package com.tu.health.data.repository

import com.tu.health.data.remote.api.InsightsAPI
import com.tu.health.data.remote.dto.insights.InsightsSummaryDTO
import com.tu.health.data.remote.dto.insights.NutritionDetailsDTO
import javax.inject.Inject

class InsightsRepository @Inject constructor(
    private val api: InsightsAPI
) {

    suspend fun getSummary(days: Int): Result<InsightsSummaryDTO> =
        safeCall { api.getSummary(query = days) }

    suspend fun getNutrition(days: Int): Result<NutritionDetailsDTO> =
        safeCall { api.getNutrition(query = days) }
}
