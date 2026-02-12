package com.tu.health.data.remote.api

import com.tu.health.data.remote.dto.HealthDailySnapshotDTO
import com.tu.health.data.remote.dto.HealthScoreDTO
import com.tu.health.data.remote.dto.RecommendationsResponseDTO
import com.tu.health.data.remote.dto.TrackedFoodDTO
import com.tu.health.data.remote.request.HealthSnapshotRequest
import com.tu.health.data.remote.request.RecommendationsRequest
import com.tu.health.data.remote.request.TrackedFoodRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HealthAPI {
    @POST("health/snapshot/")
    suspend fun createHealthSnapshot(
        @Body request: HealthSnapshotRequest
    ) : HealthDailySnapshotDTO

    @GET("health/score/")
    suspend fun getHealthScore() : HealthScoreDTO

    @GET("health/recommendations/")
    suspend fun getRecommendations() : RecommendationsResponseDTO

    @PATCH("health/recommendations/{id}/")
    suspend fun updateRecommendation(
        @Path("id") id: Int,
        @Body request: RecommendationsRequest
    ) : RecommendationsResponseDTO

}
