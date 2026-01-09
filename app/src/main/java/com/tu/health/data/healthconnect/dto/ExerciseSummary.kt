package com.tu.health.data.healthconnect.dto

data class ExerciseSummary(
    val totalDurationMinutes: Long,
    val totalActiveCaloriesKcal: Double?,
    val sessions: List<ExerciseItem>
)
