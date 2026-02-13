package com.tu.health.viewmodels.nutrition

sealed interface MacrosUiEvent {
    data class ShowMessage(val message: String) : MacrosUiEvent
}
