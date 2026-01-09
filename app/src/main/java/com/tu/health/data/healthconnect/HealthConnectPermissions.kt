package com.tu.health.data.healthconnect

import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord

/**
 * Single place to define which Health Connect read permissions your app needs.
 * Keep it minimal; add only what you really use.
 */
val HealthReadPermissions: Set<String> = setOf(
    // Steps
    HealthPermission.getReadPermission(StepsRecord::class),

    // Heart rate + HRV
    HealthPermission.getReadPermission(HeartRateRecord::class),
    HealthPermission.getReadPermission(HeartRateVariabilityRmssdRecord::class),

    // Sleep sessions
    HealthPermission.getReadPermission(SleepSessionRecord::class),

    // Exercise / activity sessions
    HealthPermission.getReadPermission(ExerciseSessionRecord::class),
)
