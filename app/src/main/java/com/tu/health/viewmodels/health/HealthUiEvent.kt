package com.tu.health.viewmodels.health


sealed class HealthUiEvent {
    data class ShowMessage(val message: String) : HealthUiEvent()
}