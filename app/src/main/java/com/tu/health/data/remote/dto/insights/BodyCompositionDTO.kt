package com.tu.health.data.remote.dto.insights

import com.tu.health.data.remote.dto.insights.BodyCompositionPointDTO
import com.tu.health.data.remote.dto.insights.BodyCompositionSummaryDTO

data class BodyCompositionDTO (
    val summary: BodyCompositionSummaryDTO,
    val points: List<BodyCompositionPointDTO>
)