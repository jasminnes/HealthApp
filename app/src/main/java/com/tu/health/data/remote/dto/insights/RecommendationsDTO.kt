package com.tu.health.data.remote.dto.insights

data class RecommendationsDTO (
    val summary: RecommendationsSummaryDTO,
    val points: List<RecommendationsPointDTO>
)
