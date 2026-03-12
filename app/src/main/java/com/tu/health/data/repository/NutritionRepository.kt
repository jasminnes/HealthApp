package com.tu.health.data.repository

import com.tu.health.data.remote.api.NutritionAPI
import com.tu.health.data.remote.dto.MacroPlanDTO
import com.tu.health.data.remote.dto.SearchedFoodDTO
import com.tu.health.data.remote.dto.TrackedFoodDTO
import com.tu.health.data.remote.request.TrackedFoodRequest
import javax.inject.Inject

class NutritionRepository @Inject constructor(
    private val api: NutritionAPI,
) {
    suspend fun getMacroPlan(): Result<MacroPlanDTO> =
        safeCall { api.getMacroPlan() }

    suspend fun searchFood(search: String): Result<List<SearchedFoodDTO>> =
        safeCall { api.getFoodSearch(query = search) }

    suspend fun getTodayFood(): Result<List<TrackedFoodDTO>> =
        safeCall { api.getTodayTrackedFood() }

    suspend fun createFood(
        name: String,
        quantity: Float,
        calories: Float,
        carbs: Float,
        fats: Float,
        protein: Float,
        unit: String
    ): Result<TrackedFoodDTO> {
        val request = TrackedFoodRequest(
            name = name,
            quantity = quantity,
            calories = calories,
            fat = fats,
            protein = protein,
            carbs = carbs,
            unit = unit
        )
        return safeCall { api.createTrackedFood(request = request) }
    }

    suspend fun updateFood(
        id: Int,
        name: String,
        quantity: Float,
        calories: Float,
        carbs: Float,
        fats: Float,
        protein: Float,
        unit: String
    ): Result<TrackedFoodDTO> {
        val request = TrackedFoodRequest(
            name = name,
            quantity = quantity,
            calories = calories,
            fat = fats,
            protein = protein,
            carbs = carbs,
            unit = unit
        )
        return safeCall { api.updateTrackedFood(id = id, request = request) }
    }

    suspend fun deleteFood(id: Int): Result<Unit> =
        safeCallUnit { api.deleteTrackedFood(id = id) }
}
