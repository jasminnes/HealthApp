package com.tu.health.viewmodels.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> get() = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> get() = _password

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> get() = _newPassword

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun onEmailChange(newEmail: String) { _email.value = newEmail }
    fun onPasswordChange(password: String) { _password.value = password }
    fun onNewPasswordChange(newPassword: String) { _newPassword.value = newPassword }

    fun login(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            val result = authRepository.loginUser(
                email = email.value,
                password = password.value
            )

            _isLoading.value = false

            result.onSuccess {
                onSuccess()
            }.onFailure { e ->
                val rawMessage = try {
                    if (e is retrofit2.HttpException) {
                        e.response()?.errorBody()?.string() ?: e.localizedMessage ?: "Unknown error"
                    } else {
                        e.localizedMessage ?: "Unknown error"
                    }
                } catch (_: Exception) {
                    e.localizedMessage ?: "Unknown error"
                }

                val customMessage = when {
                    rawMessage.contains("no active account", ignoreCase = true) ->
                        "No active account found with these credentials."

                    rawMessage.contains("invalid", ignoreCase = true) ||
                            rawMessage.contains("credentials", ignoreCase = true) ->
                        "Invalid email or password. Please try again."

                    rawMessage.contains("email", ignoreCase = true) ->
                        "Please enter a valid email address."

                    rawMessage.contains("password", ignoreCase = true) ->
                        "Incorrect password. Please try again."

                    rawMessage.contains("timeout", ignoreCase = true) ->
                        "Server timeout. Please try again later."

                    rawMessage.contains("failed to connect", ignoreCase = true) ->
                        "Unable to reach the server. Please check your internet connection."

                    rawMessage.contains("400", ignoreCase = true) ->
                        "Invalid login details. Please review your inputs."

                    rawMessage.contains("401", ignoreCase = true) ->
                        "Unauthorized access. Please check your credentials."

                    else ->
                        "Login failed. Please verify your information and try again."
                }
                onError(customMessage)
            }
        }
    }

    fun logout(
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                authRepository.logout()
            } catch (_: Exception) {
            } finally {
                _email.value = ""
                _password.value = ""
                _isLoading.value = false

                onComplete()
            }
        }
    }

    fun get(onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            val result = authRepository.getUser()
            result.onSuccess {
                onResult(true, null)
            }.onFailure {
                onResult(false, it.localizedMessage)
            }
        }
    }

    fun changePassword(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.changePassword(
                oldPassword = password.value,
                newPassword = newPassword.value
            )
            result.onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.localizedMessage) }
        }
    }

    fun deleteUser(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.delete()
            result.onSuccess {
                _email.value = ""
                _password.value = ""
                onResult(true, null)
            }.onFailure { onResult(false, it.localizedMessage) }
        }
    }
}
