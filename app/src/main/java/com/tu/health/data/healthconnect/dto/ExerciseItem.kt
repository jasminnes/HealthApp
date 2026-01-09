package com.tu.health.data.healthconnect.dto

data class ExerciseItem(
    val exerciseType: Int,
    val durationMinutes: Long,
    val activeCaloriesKcal: Double?,
)