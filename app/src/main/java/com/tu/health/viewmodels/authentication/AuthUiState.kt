package com.tu.health.viewmodels.authentication

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val newPassword: String = "",
    val isLoading: Boolean = false
)
