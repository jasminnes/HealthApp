package com.tu.health.viewmodels.insights.scores

sealed interface HealthScoresDetailsEvent {
    data object Load : HealthScoresDetailsEvent
    data object Refresh : HealthScoresDetailsEvent
    data class ChangeDays(val days: Int) : HealthScoresDetailsEvent
    data object ClearError : HealthScoresDetailsEvent
}