package com.tu.health.data.remote.api

import com.tu.health.data.remote.dto.MacroPlanDTO
import com.tu.health.data.remote.dto.SearchedFoodDTO
import com.tu.health.data.remote.dto.TrackedFoodDTO
import com.tu.health.data.remote.request.TrackedFoodRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NutritionAPI {
    @GET("nutrition/macros/")
    suspend fun getMacroPlan(): MacroPlanDTO

    @GET("nutrition/search/")
    suspend fun getFoodSearch(
        @Query("q") query: String
    ) : List<SearchedFoodDTO>

    @GET("nutrition/track-food/today/")
    suspend fun getTodayTrackedFood() : List<TrackedFoodDTO>

    @POST("nutrition/track-food/")
    suspend fun createTrackedFood(
        @Body request: TrackedFoodRequest
    ) : TrackedFoodDTO

    @PUT("nutrition/track-food/{id}/")
    suspend fun updateTrackedFood(
        @Path("id") id: Int,
        @Body request: TrackedFoodRequest
    ) : TrackedFoodDTO

    @DELETE("nutrition/track-food/{id}/")
    suspend fun deleteTrackedFood(
        @Path("id") id: Int
    )
}