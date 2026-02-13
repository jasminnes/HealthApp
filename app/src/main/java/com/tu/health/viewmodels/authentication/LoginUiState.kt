package com.tu.health.viewmodels.authentication

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val newPassword: String = "",
    val isLoading: Boolean = false
)
