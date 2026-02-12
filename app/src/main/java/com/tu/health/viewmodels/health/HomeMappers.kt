package com.tu.health.viewmodels.health

import com.tu.health.data.remote.dto.HealthScoreDTO
import com.tu.health.data.remote.dto.RecommendationsDTO
import com.tu.health.data.remote.dto.RecommendationsResponseDTO

internal fun Float?.orZero(): Float = this ?: 0f

internal fun String?.toRecommendationStatus(): RecommendationStatus =
    when (this?.trim()?.lowercase()) {
        "dismissed" -> RecommendationStatus.DISMISSED
        "completed" -> RecommendationStatus.COMPLETED
        else -> RecommendationStatus.NEW
    }

internal fun HealthScoreDTO.toUi(): HealthScoreUi =
    HealthScoreUi(
        total = total.orZero(),
        activity = activity.orZero(),
        recovery = recovery.orZero(),
        nutrition = nutrition.orZero(),
        bodyComposition = bodyComposition.orZero(),
        isStale = isStale,
        status = status,
        requestedDate = requestedDate,
    )

internal fun RecommendationsResponseDTO.toUiList(): List<RecommendationUi> =
    recommendations
        .filterNotNull()
        .map { it.toUi() }
        .sortedByDescending { it.priority }

internal fun RecommendationsDTO.toUi(): RecommendationUi =
    RecommendationUi(
        id = id,
        date = date,
        category = category,
        title = title,
        message = message,
        reason = reason,
        priority = priority,
        status = status.toRecommendationStatus(),
        evidence = evidence
    )
