package com.tu.health.data.repository

import com.tu.health.data.healthconnect.dto.HealthSnapshot
import com.tu.health.data.remote.dto.HealthDailySnapshotDTO
import com.tu.health.data.remote.request.ExerciseRequest
import com.tu.health.data.remote.request.HealthSnapshotRequest
import com.tu.health.data.remote.request.HeartRateRequest
import com.tu.health.data.remote.request.HrvRequest
import com.tu.health.data.remote.request.SleepRequest
import javax.inject.Inject
import com.tu.health.data.remote.api.HealthAPI
import com.tu.health.data.remote.dto.HealthScoreDTO
import com.tu.health.data.remote.dto.RecommendationsResponseDTO
import com.tu.health.data.remote.request.RecommendationsRequest

class HealthRepository @Inject constructor(
    private val api: HealthAPI
) {

    suspend fun createSnapshot(
        date: String,
        snapshot: HealthSnapshot
    ): Result<HealthDailySnapshotDTO> {

        val request = HealthSnapshotRequest(
            date = date,
            todaySteps = snapshot.todaySteps.toInt(),

            heartRateToday = HeartRateRequest(
                minBpm = snapshot.heartRateToday.minBpm ?: 0,
                maxBpm = snapshot.heartRateToday.maxBpm ?: 0,
                avgBpm = snapshot.heartRateToday.avgBpm ?: 0,
                latestBpm = snapshot.heartRateToday.latestBpm ?: 0
            ),

            hrvToday = HrvRequest(
                avgRmssdMs = snapshot.hrvToday.avgRmssdMs ?: 0f,
                latestRmssdMs = snapshot.hrvToday.latestRmssdMs ?: 0f
            ),

            latestSleep = SleepRequest(
                durationMinutes = snapshot.latestSleep.durationMinutes ?: 0
            ),

            exerciseToday = ExerciseRequest(
                totalDurationMinutes = snapshot.exerciseToday.totalDurationMinutes,
                totalActiveCaloriesKcal =  snapshot.exerciseToday.totalActiveCaloriesKcal ?: 0f,
                sessionsCount =  snapshot.exerciseToday.sessions.toList().count()
            )
        )

        return safeCall { api.createHealthSnapshot(request = request) }
    }

    suspend fun getHealthScore(): Result<HealthScoreDTO> =
        safeCall { api.getHealthScore() }

    suspend fun getRecommendations(): Result<RecommendationsResponseDTO> =
        safeCall { api.getRecommendations() }


    suspend fun updateRecommendation(
        id: Int,
        status: String
    ): Result<RecommendationsResponseDTO> {
        val request = RecommendationsRequest(
            status = status,
        )

        return safeCall { api.updateRecommendation(id, request) }
    }

}
