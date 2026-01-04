package com.tu.health.data.remote

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
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfileAPI {

    @POST("user/onboarding/")
    suspend fun onboardUser(
        @Header("Authorization") bearerToken: String,
        @Body request: OnboardingRequest
    ): ProfileDTO

    @GET("user/profile/")
    suspend fun getProfile(
        @Header("Authorization") bearerToken: String
    ): ProfileDTO

    @PUT("user/profile")
    suspend fun updateProfile(
        @Header("Authorization") bearerToken: String,
        @Body request: ProfileDTO
    ): ProfileDTO

    @PATCH("user/profile")
    suspend fun updateUserHeight(
        @Header("Authorization") bearerToken: String,
        @Body request: UpdateUserHeightRequest
    ): ProfileDTO

    @PATCH("user/profile")
    suspend fun updateUserDietType(
        @Header("Authorization") bearerToken: String,
        @Body request: UpdateUserDietTypeRequest
    ): ProfileDTO

    @PATCH("user/profile")
    suspend fun updateUserActivityLevel(
        @Header("Authorization") bearerToken: String,
        @Body request: UpdateUserActivityLevelRequest
    ): ProfileDTO

    @PATCH("user/profile")
    suspend fun updateUserConditions(
        @Header("Authorization") bearerToken: String,
        @Body request: UpdateUserConditionsRequest
    ): ProfileDTO

    @GET("user/measurements/")
    suspend fun getBodyMeasurementsAll(
        @Header("Authorization") bearerToken: String
    ): List<BodyMeasurementDTO>

    @POST("user/measurements/")
    suspend fun createBodyMeasurement(
        @Header("Authorization") bearerToken: String,
        @Body request: BodyMeasurementRequest
    ): BodyMeasurementDTO

    @DELETE("/user/measurements/{id}/")
    suspend fun deleteBodyMeasurement(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
    )

    @GET("user/conditions/")
    suspend fun getAllConditions(
        @Header("Authorization") bearerToken: String
    ): List<ConditionDTO>

    @GET("/diets/type/")
    suspend fun getAllDietTypes(
        @Header("Authorization") bearerToken: String
    ): List<DietTypeDTO>

    @GET("/activity/level/")
    suspend fun getAllActivityLevels(
        @Header("Authorization") bearerToken: String
    ): List<ActivityDTO>
}
