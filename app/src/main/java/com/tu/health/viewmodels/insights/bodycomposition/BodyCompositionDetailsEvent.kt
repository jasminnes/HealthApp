package com.tu.health.viewmodels.insights.bodycomposition

sealed interface BodyCompositionDetailsEvent {
    data object Load : BodyCompositionDetailsEvent
    data object Refresh : BodyCompositionDetailsEvent
    data class ChangeDays(val days: Int) : BodyCompositionDetailsEvent
    data object ClearError : BodyCompositionDetailsEvent
}