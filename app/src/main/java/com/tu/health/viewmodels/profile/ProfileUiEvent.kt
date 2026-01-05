package com.tu.health.viewmodels.profile

sealed interface ProfileUiEvent {
    data class ShowMessage(val message: String) : ProfileUiEvent
}
