package com.tu.health.data.remote.dto.insights.healthconnect

data class HealthConnectDTO (
    val summary: HealthConnectSummaryDTO,
    val points: List<HealthConnectPointDTO>
)
