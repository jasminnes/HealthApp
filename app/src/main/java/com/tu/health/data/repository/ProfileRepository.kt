package com.tu.health.data.repository

import com.tu.health.data.remote.api.ProfileAPI
import com.tu.health.data.remote.dto.ActivityDTO
import com.tu.health.data.remote.dto.BodyMeasurementDTO
import com.tu.health.data.remote.dto.ConditionDTO
import com.tu.health.data.remote.dto.DietTypeDTO
import com.tu.health.data.remote.dto.ProfileDTO
import com.tu.health.data.remote.request.BodyMeasurementRequest
import com.tu.health.data.remote.request.ProfileRequest
import com.tu.health.data.remote.request.UpdateUserConditionsRequest
import com.tu.health.data.remote.request.UpdateUserDietTypeRequest
import com.tu.health.data.remote.request.UpdateUserHeightRequest
import com.tu.health.data.remote.request.UpdateUserWeightGoalRequest
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val api: ProfileAPI,
) {
    suspend fun getProfile(): Result<ProfileDTO> =
        safeCall { api.getProfile() }

    suspend fun updateProfile(
        height: Float,
        activityLevel: Int,
        dietType: Int,
        conditions: List<Int>,
        weightGoal: String,
        weight: Float,
        neck: Float,
        waist: Float
    ): Result<ProfileDTO> {
        val request = ProfileRequest(
            height = height,
            activityLevel = activityLevel,
            dietType = dietType,
            conditions = conditions,
            weightGoal = weightGoal,
            bodyMeasurements = BodyMeasurementRequest(
                weight = weight,
                neck = neck,
                waist = waist
            )
        )
        return safeCall { api.updateProfile(request) }
    }

    suspend fun updateUserHeight(height: Float): Result<ProfileDTO> {
        val request = UpdateUserHeightRequest(height = height)
        return safeCall { api.updateUserHeight(request) }
    }

    suspend fun updateUserWeightGoal(goal: String): Result<ProfileDTO> {
        val request = UpdateUserWeightGoalRequest(goal = goal)
        return safeCall { api.updateUserWeightGoal(request) }
    }

    suspend fun updateUserDietType(dietType: Int?): Result<ProfileDTO> {
        val request = UpdateUserDietTypeRequest(dietType = dietType)
        return safeCall { api.updateUserDietType(request) }
    }

    suspend fun updateUserConditions(conditions: List<Int>): Result<ProfileDTO> {
        val request = UpdateUserConditionsRequest(conditions = conditions)
        return safeCall { api.updateUserConditions(request) }
    }

    suspend fun getAllConditions(): Result<List<ConditionDTO>> =
        safeCall { api.getAllConditions() }

    suspend fun getAllDietTypes(): Result<List<DietTypeDTO>> =
        safeCall { api.getAllDietTypes() }

    suspend fun getRecommendedDiets(): Result<List<DietTypeDTO>> =
        safeCall { api.getRecommendedDiets() }

    suspend fun getAllActivityLevels(): Result<List<ActivityDTO>> =
        safeCall { api.getAllActivityLevels() }

    suspend fun getAllBodyMeasurements(): Result<List<BodyMeasurementDTO>> =
        safeCall { api.getBodyMeasurementsAll() }

    suspend fun createBodyMeasurement(
        weight: Float,
        neck: Float,
        waist: Float
    ): Result<BodyMeasurementDTO> {
        val request = BodyMeasurementRequest(
            weight = weight,
            neck = neck,
            waist = waist
        )
        return safeCall { api.createBodyMeasurement(request) }
    }

    suspend fun deleteBodyMeasurement(id: Int): Result<Unit> =
        safeCallUnit { api.deleteBodyMeasurement(id) }
}
