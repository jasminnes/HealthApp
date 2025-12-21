package com.tu.health.data.repository

import com.tu.health.data.local.SecureTokenStore
import com.tu.health.data.remote.ProfileAPI
import com.tu.health.data.remote.dto.ActivityDTO
import com.tu.health.data.remote.dto.BodyMeasurementDTO
import com.tu.health.data.remote.dto.ConditionDTO
import com.tu.health.data.remote.dto.DietTypeDTO
import com.tu.health.data.remote.dto.ProfileDTO
import com.tu.health.data.remote.dto.request.BodyMeasurementRequest
import com.tu.health.data.remote.dto.request.OnboardingRequest
import com.tu.health.data.remote.dto.request.UpdateUserActivityLevelRequest
import com.tu.health.data.remote.dto.request.UpdateUserConditionsRequest
import com.tu.health.data.remote.dto.request.UpdateUserDietTypeRequest
import com.tu.health.data.remote.dto.request.UpdateUserHeightRequest
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val api: ProfileAPI,
    private val secureTokenStore: SecureTokenStore,
) {

    suspend fun getProfile(): Result<ProfileDTO> {
        return try {
            val response = api.getProfile(
                "Bearer ${secureTokenStore.accessToken.first()}"
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(
        height: Float,
        activityLevel: Int,
        dietType: Int,
        conditions: List<Int>
    ): Result<ProfileDTO> {
        return try {
            val request = ProfileDTO(
                height = height,
                createdDate = "",
                activityLevel = activityLevel,
                dietType = dietType,
                conditions = conditions
            )

            val response = api.updateProfile(
                "Bearer ${secureTokenStore.accessToken.first()}", request
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserHeight(
        height: Float
    ): Result<ProfileDTO> {
        return try {
            val request = UpdateUserHeightRequest(
                height = height,
            )

            val response = api.updateUserHeight(
                "Bearer ${secureTokenStore.accessToken.first()}", request
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserDietType(
        dietType: Int?
    ): Result<ProfileDTO> {
        return try {
            val request = UpdateUserDietTypeRequest(
                dietType = dietType
            )

            val response = api.updateUserDietType(
                "Bearer ${secureTokenStore.accessToken.first()}", request
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserActivityLevel(
        activityLevel: Int?
    ): Result<ProfileDTO> {
        return try {
            val request = UpdateUserActivityLevelRequest(
                activityLevel = activityLevel
            )

            val response = api.updateUserActivityLevel(
                "Bearer ${secureTokenStore.accessToken.first()}", request
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserConditions(
        conditions: List<Int>
    ): Result<ProfileDTO> {
        return try {
            val request = UpdateUserConditionsRequest(
                conditions = conditions
            )

            val response = api.updateUserConditions(
                "Bearer ${secureTokenStore.accessToken.first()}", request
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllConditions(): Result<List<ConditionDTO>> {
        return try {
            val response = api.getAllConditions(
                "Bearer ${secureTokenStore.accessToken.first()}"
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllDietTypes(): Result<List<DietTypeDTO>> {
        return try {
            val response = api.getAllDietTypes(
                "Bearer ${secureTokenStore.accessToken.first()}"
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllActivityLevels(): Result<List<ActivityDTO>> {
        return try {
            val response = api.getAllActivityLevels(
                "Bearer ${secureTokenStore.accessToken.first()}"
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllBodyMeasurements(): Result<List<BodyMeasurementDTO>> {
        return try {
            val response = api.getBodyMeasurementsAll(
                "Bearer ${secureTokenStore.accessToken.first()}"
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLatestBodyMeasurement(): Result<BodyMeasurementDTO> {
        return try {
            val response = api.getLatestBodyMeasurement(
                "Bearer ${secureTokenStore.accessToken.first()}"
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createBodyMeasurement(
        weight: Float,
        neck: Float,
        waist: Float
    ): Result<BodyMeasurementDTO> {
        return try {
            val request = BodyMeasurementRequest(
                weight = weight,
                neck = neck,
                waist = waist
            )

            val response = api.createBodyMeasurement(
                "Bearer ${secureTokenStore.accessToken.first()}", request
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBodyMeasurement(
        id: Int,
        weight: Float,
        neck: Float,
        waist: Float
    ): Result<BodyMeasurementDTO> {
        return try {
            val request = BodyMeasurementRequest(
                weight = weight,
                neck = neck,
                waist = waist
            )

            val response = api.updateBodyMeasurement(
                "Bearer ${secureTokenStore.accessToken.first()}", id, request
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBodyMeasurement(
        id: Int
    ): Result<Unit> {
        return try {
            api.deleteBodyMeasurement(
                "Bearer ${secureTokenStore.accessToken.first()}",
                id
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun onboardUser(
        height: Float,
        activityLevel: Int,
        dietType: Int,
        weight: Float,
        neck: Float,
        waist: Float,
        conditions: List<Int>
    ): Result<ProfileDTO> {
        return try {
            val request = OnboardingRequest(
                height = height,
                activityLevel = activityLevel,
                dietType = dietType,
                conditions = conditions,
                bodyMeasurements = BodyMeasurementRequest(
                    weight = weight,
                    neck = neck,
                    waist = waist
                )
            )

            val response = api.onboardUser(
                "Bearer ${secureTokenStore.accessToken.first()}",
                request
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
