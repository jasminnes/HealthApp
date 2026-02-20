package com.tu.health.viewmodels.insights.healthconnect

sealed interface HealthConnectDetailsEvent {
    data object Load : HealthConnectDetailsEvent
    data object Refresh : HealthConnectDetailsEvent
    data class ChangeDays(val days: Int) : HealthConnectDetailsEvent
    data object ClearError : HealthConnectDetailsEvent
}