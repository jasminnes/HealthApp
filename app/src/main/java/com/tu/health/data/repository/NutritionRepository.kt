package com.tu.health.data.repository

import com.tu.health.data.local.SecureTokenStore
import com.tu.health.data.remote.NutritionAPI
import com.tu.health.data.remote.dto.DailyMacroSummaryDTO
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class NutritionRepository @Inject constructor(
    private val api: NutritionAPI,
    private val secureTokenStore: SecureTokenStore,
) {

    suspend fun getDailyMacroSummary(): Result<DailyMacroSummaryDTO> {
        return try {
            val response = api.getDailyMacroSummary(
                "Bearer ${secureTokenStore.accessToken.first()}"
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
