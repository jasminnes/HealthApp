package com.tu.health.viewmodels.health


sealed class HomeUiEvent {
    data class ShowMessage(val message: String) : HomeUiEvent()
}