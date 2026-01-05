package com.tu.health.viewmodels.authentication

sealed interface AuthUiEvent {
    data class ShowMessage(val message: String) : AuthUiEvent
}
