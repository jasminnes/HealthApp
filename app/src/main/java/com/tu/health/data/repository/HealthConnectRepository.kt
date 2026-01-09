package com.tu.health.data.repository

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class HealthConnectRepository @Inject constructor(
    private val client: HealthConnectClient
) {
    suspend fun hasAllPermissions(permissions: Set<String>): Boolean = withContext(Dispatchers.IO) {
        val granted = client.permissionController.getGrantedPermissions()
        granted.containsAll(permissions)
    }

    suspend fun readTodayStepsTotal(): Long = withContext(Dispatchers.IO) {
        val zone = ZoneId.systemDefault()
        val start = LocalDate.now(zone).atStartOfDay(zone).toInstant()
        val end = LocalDate.now(zone).plusDays(1).atStartOfDay(zone).toInstant()

        val response = client.aggregate(
            AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        response[StepsRecord.COUNT_TOTAL] ?: 0L
    }
}
