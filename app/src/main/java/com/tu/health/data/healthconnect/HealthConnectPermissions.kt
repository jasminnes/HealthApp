package com.tu.health.data.healthconnect

import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord

val StepsReadPermissions: Set<String> = setOf(
    HealthPermission.getReadPermission(StepsRecord::class)
)
