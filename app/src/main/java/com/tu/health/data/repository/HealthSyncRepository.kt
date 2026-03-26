package com.tu.health.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.tu.health.data.healthconnect.HealthReadPermissions
import com.tu.health.data.local.ProfileDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject


sealed class HealthSyncResult {
    data object SkippedAlreadySyncedToday : HealthSyncResult()
    data class Synced(val uploadedDate: String) : HealthSyncResult()
    data class SkippedNoPermissions(val reason: String = "Health Connect permissions not granted") : HealthSyncResult()
    data class Failed(val reason: String) : HealthSyncResult()
}

class HealthSyncRepository @Inject constructor(
    private val profileDataStore: ProfileDataStore,
    private val healthConnectRepository: HealthConnectRepository,
    private val healthRepository: HealthRepository
) {

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun syncYesterdayOncePerDay(): HealthSyncResult = withContext(Dispatchers.IO) {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val todayIso = today.toString()

        val lastSyncedDay = profileDataStore.lastHealthUploadDayFlow.first()
        if (lastSyncedDay == todayIso) {
            return@withContext HealthSyncResult.SkippedAlreadySyncedToday
        }

        val hasPerms = try {
            healthConnectRepository.hasAllPermissions(HealthReadPermissions)
        } catch (_: Throwable) {
            false
        }
        if (!hasPerms) {
            return@withContext HealthSyncResult.SkippedNoPermissions()
        }

        val yesterday = today.minusDays(1)
        val yesterdayIso = yesterday.toString()

        return@withContext try {
            val snapshot = healthConnectRepository.readHealthSnapshotForDate(yesterday)
            healthRepository.createSnapshot(date = yesterdayIso, snapshot = snapshot)

            profileDataStore.saveLastHealthUploadDay(todayIso)

            HealthSyncResult.Synced(uploadedDate = yesterdayIso)
        } catch (t: Throwable) {
            HealthSyncResult.Failed(t.message ?: "Unknown sync error")
        }
    }
}
