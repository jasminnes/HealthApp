package com.tu.health.data.remote

import com.tu.health.data.remote.dto.MacroPlanDTO
import com.tu.health.data.remote.dto.SearchedFoodDTO
import com.tu.health.data.remote.dto.TrackedFoodDTO
import com.tu.health.data.remote.dto.request.TrackedFoodRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NutritionAPI {
    @GET("/nutrition/macros/")
    suspend fun getMacroPlan(
        @Header("Authorization") bearerToken: String
    ): MacroPlanDTO

    @GET("/nutrition/search/")
    suspend fun getFoodSearch(
        @Header("Authorization") bearerToken: String,
        @Query("q") query: String
    ) : List<SearchedFoodDTO>

    @GET("/nutrition/track-food/")
    suspend fun getAllTrackedFood(
        @Header("Authorization") bearerToken: String
    ) : List<TrackedFoodDTO>

    @GET("/nutrition/track-food/today/")
    suspend fun getTodayTrackedFood(
        @Header("Authorization") bearerToken: String
    ) : List<TrackedFoodDTO>

    @POST("/nutrition/track-food/")
    suspend fun createTrackedFood(
        @Header("Authorization") bearerToken: String,
        @Body request: TrackedFoodRequest
    ) : TrackedFoodDTO

    @PUT("/nutrition/track-food/{id}/")
    suspend fun updateTrackedFood(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
        @Body request: TrackedFoodRequest
    ) : TrackedFoodDTO

    @DELETE("/nutrition/track-food/{id}/")
    suspend fun deleteTrackedFood(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int
    )

}