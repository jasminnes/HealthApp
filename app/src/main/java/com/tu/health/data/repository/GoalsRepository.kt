package com.tu.health.data.repository

import com.tu.health.data.local.SecureTokenStore
import com.tu.health.data.remote.GoalsAPI
import com.tu.health.data.remote.dto.request.CreateWeightGoalRequest
import com.tu.health.data.remote.dto.request.UpdateWeightGoalRequest
import com.tu.health.data.remote.dto.WeightGoalDTO
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GoalsRepository @Inject constructor(
    private val api: GoalsAPI,
    private val secureTokenStore: SecureTokenStore,
) {

    suspend fun getAllWeightGoals(): Result<List<WeightGoalDTO>> {
        return try {
            val response = api.getAllWeightGoals(
                "Bearer ${secureTokenStore.accessToken.first()}"
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWeightGoal(id: Int): Result<WeightGoalDTO> {
        return try {
            val response = api.getWeightGoal(
                "Bearer ${secureTokenStore.accessToken.first()}", id
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createWeightGoal(
        name: String,
        finalDate: String,
        startingWeight: Float,
        goalWeight: Float
    ): Result<WeightGoalDTO> {
        return try {
            val request = CreateWeightGoalRequest(
                name = name,
                goalWeight = goalWeight,
                finalDate = finalDate,
                startingWeight = startingWeight
            )

            val response = api.createWeightGoal(
                "Bearer ${secureTokenStore.accessToken.first()}", request
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateWeightGoal(
        id: Int,
        name: String,
        finalDate: String,
        goalWeight: Float
    ): Result<WeightGoalDTO> {
        return try {
            val request = UpdateWeightGoalRequest(
                name = name,
                goalWeight = goalWeight,
                finalDate = finalDate
            )

            val response = api.updateWeightGoal(
                "Bearer ${secureTokenStore.accessToken.first()}", id, request
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteWeightGoal(
        id: Int
    ): Result<Unit> {
        return try {
            api.deleteWeightGoal(
                "Bearer ${secureTokenStore.accessToken.first()}",
                id
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
