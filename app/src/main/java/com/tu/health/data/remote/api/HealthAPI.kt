package com.tu.health.data.remote.api

import com.tu.health.data.remote.dto.HealthDailySnapshotDTO
import com.tu.health.data.remote.request.HealthSnapshotRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface HealthAPI {
    @POST("health/snapshot/")
    suspend fun createHealthSnapshot(
        @Body request: HealthSnapshotRequest
    ) : HealthDailySnapshotDTO
}
