package com.tu.health.data.repository

import com.tu.health.data.local.SecureTokenStore
import com.tu.health.data.remote.NutritionAPI
import com.tu.health.data.remote.dto.MacroPlanDTO
import com.tu.health.data.remote.dto.SearchedFoodDTO
import com.tu.health.data.remote.dto.TrackedFoodDTO
import com.tu.health.data.remote.dto.request.TrackedFoodRequest
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class NutritionRepository @Inject constructor(
    private val api: NutritionAPI,
    private val secureTokenStore: SecureTokenStore,
) {

    suspend fun getMacroPlan(): Result<MacroPlanDTO> {
        return try {
            val response = api.getMacroPlan(
                "Bearer ${secureTokenStore.accessToken.first()}"
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchFood(
        search: String
    ): Result<List<SearchedFoodDTO>> {
        return try {
            val response = api.getFoodSearch(
                "Bearer ${secureTokenStore.accessToken.first()}",
                query = search
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllFood(): Result<List<TrackedFoodDTO>> {
        return try {
            val response = api.getAllTrackedFood(
                "Bearer ${secureTokenStore.accessToken.first()}",
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createFood(
        name: String,
        quantity: Float,
        calories: Float,
        carbs: Float,
        fats: Float,
        protein: Float
    ): Result<TrackedFoodDTO> {
        return try {
            val request = TrackedFoodRequest(
                name = name,
                quantity = quantity,
                calories = calories,
                fat = fats,
                protein = protein,
                carbs = carbs
            )

            val response = api.createTrackedFood(
                "Bearer ${secureTokenStore.accessToken.first()}",
                request = request
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateFood(
        id: Int,
        name: String,
        quantity: Float,
        calories: Float,
        carbs: Float,
        fats: Float,
        protein: Float
    ): Result<TrackedFoodDTO> {
        return try {
            val request = TrackedFoodRequest(
                name = name,
                quantity = quantity,
                calories = calories,
                fat = fats,
                protein = protein,
                carbs = carbs
            )

            val response = api.updateTrackedFood(
                "Bearer ${secureTokenStore.accessToken.first()}",
                id = id,
                request = request
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteFood(
        id: Int
    ): Result<Unit> {
        return try {
            val response = api.deleteTrackedFood(
                "Bearer ${secureTokenStore.accessToken.first()}",
                id = id
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
