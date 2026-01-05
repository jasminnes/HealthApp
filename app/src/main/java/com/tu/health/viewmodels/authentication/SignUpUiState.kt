package com.tu.health.viewmodels.authentication

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val gender: String = "",
    val birthDate: String = "",
    val isLoading: Boolean = false
)
