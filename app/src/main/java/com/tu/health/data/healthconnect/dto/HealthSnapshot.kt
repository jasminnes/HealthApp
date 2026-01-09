package com.tu.health.data.healthconnect.dto

data class HealthSnapshot(
    val todaySteps: Long,
    val heartRateToday: HeartRateDaySummary,
    val hrvToday: HrvDaySummary,
    val latestSleep: SleepSummary,
    val exerciseToday: ExerciseSummary
)
