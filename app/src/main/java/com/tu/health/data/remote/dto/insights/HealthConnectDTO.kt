package com.tu.health.data.remote.dto.insights

import com.tu.health.data.remote.dto.insights.HealthConnectPointDTO
import com.tu.health.data.remote.dto.insights.HealthConnectSummaryDTO

data class HealthConnectDTO (
    val summary: HealthConnectSummaryDTO,
    val points: List<HealthConnectPointDTO>
)