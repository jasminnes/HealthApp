package com.tu.health.data.remote.dto.insights

data class MetabolicDTO (
    val summary: MetabolicSummaryDTO,
    val points: List<MetabolicPointDTO>
)
