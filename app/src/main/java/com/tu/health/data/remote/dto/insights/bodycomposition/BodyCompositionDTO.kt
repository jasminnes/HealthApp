package com.tu.health.data.remote.dto.insights.bodycomposition

data class BodyCompositionDTO (
    val summary: BodyCompositionSummaryDTO,
    val points: List<BodyCompositionPointDTO>
)