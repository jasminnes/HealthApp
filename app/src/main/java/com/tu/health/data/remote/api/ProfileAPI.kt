package com.tu.health.data.remote.api

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
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfileAPI {

    @GET("user/profile/")
    suspend fun getProfile(): ProfileDTO

    @PUT("user/profile/")
    suspend fun updateProfile(
        @Body request: ProfileRequest
    ): ProfileDTO

    @PATCH("user/profile/")
    suspend fun updateUserHeight(
        @Body request: UpdateUserHeightRequest
    ): ProfileDTO

    @PATCH("user/profile/")
    suspend fun updateUserWeightGoal(
        @Body request: UpdateUserWeightGoalRequest
    ): ProfileDTO

    @PATCH("user/profile/")
    suspend fun updateUserDietType(
        @Body request: UpdateUserDietTypeRequest
    ): ProfileDTO

    @PATCH("user/profile/")
    suspend fun updateUserConditions(
        @Body request: UpdateUserConditionsRequest
    ): ProfileDTO

    @GET("user/measurements/")
    suspend fun getBodyMeasurementsAll(
    ): List<BodyMeasurementDTO>

    @POST("user/measurements/")
    suspend fun createBodyMeasurement(
        @Body request: BodyMeasurementRequest
    ): BodyMeasurementDTO

    @DELETE("user/measurements/{id}/")
    suspend fun deleteBodyMeasurement(
        @Path("id") id: Int,
    )

    @GET("user/conditions/")
    suspend fun getAllConditions(): List<ConditionDTO>

    @GET("diets/top-recommended-diets/")
    suspend fun getRecommendedDiets(): List<DietTypeDTO>

    @GET("diets/type/")
    suspend fun getAllDietTypes(): List<DietTypeDTO>

    @GET("activity/level/")
    suspend fun getAllActivityLevels(): List<ActivityDTO>
}