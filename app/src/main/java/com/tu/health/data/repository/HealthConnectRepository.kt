package com.tu.health.data.repository

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.tu.health.data.healthconnect.dto.ExerciseItem
import com.tu.health.data.healthconnect.dto.ExerciseSummary
import com.tu.health.data.healthconnect.dto.HealthSnapshot
import com.tu.health.data.healthconnect.dto.HeartRateDaySummary
import com.tu.health.data.healthconnect.dto.HrvDaySummary
import com.tu.health.data.healthconnect.dto.SleepSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.roundToInt

class HealthConnectRepository @Inject constructor(
    private val client: HealthConnectClient
) {
    suspend fun hasAllPermissions(permissions: Set<String>): Boolean = withContext(Dispatchers.IO) {
        val granted = client.permissionController.getGrantedPermissions()
        granted.containsAll(permissions)
    }

    private fun dayRange(date: LocalDate, zone: ZoneId = ZoneId.systemDefault()): Pair<Instant, Instant> {
        val start = date.atStartOfDay(zone).toInstant()
        val end = date.plusDays(1).atStartOfDay(zone).toInstant()
        return start to end
    }

    suspend fun readStepsTotal(date: LocalDate): Long = withContext(Dispatchers.IO) {
        val (start, end) = dayRange(date)
        val response = client.aggregate(
            AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        response[StepsRecord.COUNT_TOTAL] ?: 0L
    }

    suspend fun readHeartRateSummary(date: LocalDate): HeartRateDaySummary = withContext(Dispatchers.IO) {
        val (start, end) = dayRange(date)

        val records = client.readRecords(
            ReadRecordsRequest(
                recordType = HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end),
                pageSize = 500
            )
        ).records

        val samples = records.flatMap { it.samples }
        if (samples.isEmpty()) {
            return@withContext HeartRateDaySummary(null, null, null, null)
        }

        val values = samples.map { it.beatsPerMinute }.map { it.toInt() }
        val min = values.minOrNull()
        val max = values.maxOrNull()
        val avg = (values.sum().toDouble() / values.size).roundToInt()
        val latest = samples.maxByOrNull { it.time }?.beatsPerMinute?.toInt()

        HeartRateDaySummary(
            minBpm = min,
            avgBpm = avg,
            maxBpm = max,
            latestBpm = latest
        )
    }

    suspend fun readHrvRmssdSummary(date: LocalDate): HrvDaySummary = withContext(Dispatchers.IO) {
        val (start, end) = dayRange(date)

        val records = client.readRecords(
            ReadRecordsRequest(
                recordType = HeartRateVariabilityRmssdRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end),
                pageSize = 500
            )
        ).records

        if (records.isEmpty()) {
            return@withContext HrvDaySummary(null, null)
        }

        val values = records.map { it.heartRateVariabilityMillis }
        val avg = values.average()
        val latest = records.maxByOrNull { it.time }?.heartRateVariabilityMillis

        HrvDaySummary(avgRmssdMs = avg, latestRmssdMs = latest)
    }

    suspend fun readSleepSummaryForDate(date: LocalDate): SleepSummary = withContext(Dispatchers.IO) {
        val zone = ZoneId.systemDefault()

        val windowStart = date.minusDays(1).atStartOfDay(zone).toInstant()
        val windowEnd = date.plusDays(1).atStartOfDay(zone).toInstant()

        val sessions = client.readRecords(
            ReadRecordsRequest(
                recordType = SleepSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(windowStart, windowEnd),
                pageSize = 200
            )
        ).records

        val best = sessions
            .filter { s -> LocalDate.ofInstant(s.endTime, zone) == date }
            .maxByOrNull { it.endTime }
            ?: return@withContext SleepSummary(durationMinutes = null)

        val durationMinutes = Duration.between(best.startTime, best.endTime).toMinutes()
        SleepSummary(durationMinutes = durationMinutes)
    }

    private suspend fun aggregateActiveCalories(start: Instant, end: Instant): Double? {
        val response = client.aggregate(
            AggregateRequest(
                metrics = setOf(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return response[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories
    }

    suspend fun readExerciseSummary(date: LocalDate): ExerciseSummary = withContext(Dispatchers.IO) {
        val (start, end) = dayRange(date)

        val sessions = client.readRecords(
            ReadRecordsRequest(
                recordType = ExerciseSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end),
                pageSize = 200
            )
        ).records.sortedByDescending { it.endTime }

        val items = mutableListOf<ExerciseItem>()
        var totalDurationMinutes = 0L
        var totalActiveKcal: Double? = 0.0

        for (s in sessions) {
            val durMin = Duration.between(s.startTime, s.endTime).toMinutes()
            totalDurationMinutes += durMin

            val activeKcal = try { aggregateActiveCalories(s.startTime, s.endTime) } catch (_: Throwable) { null }

            items += ExerciseItem(
                exerciseType = s.exerciseType,
                durationMinutes = durMin,
                activeCaloriesKcal = activeKcal,
            )

            totalActiveKcal = when {
                totalActiveKcal == null -> null
                activeKcal == null -> null
                else -> totalActiveKcal + activeKcal
            }
        }

        if (items.isEmpty()) totalActiveKcal = null

        ExerciseSummary(
            totalDurationMinutes = totalDurationMinutes,
            totalActiveCaloriesKcal = totalActiveKcal,
            sessions = items
        )
    }

    suspend fun readHealthSnapshotForDate(date: LocalDate): HealthSnapshot = withContext(Dispatchers.IO) {
        val steps = readStepsTotal(date)
        val hr = readHeartRateSummary(date)
        val hrv = readHrvRmssdSummary(date)
        val sleep = readSleepSummaryForDate(date)
        val exercise = readExerciseSummary(date)

        HealthSnapshot(
            todaySteps = steps,
            heartRateToday = hr,
            hrvToday = hrv,
            latestSleep = sleep,
            exerciseToday = exercise
        )
    }
}
