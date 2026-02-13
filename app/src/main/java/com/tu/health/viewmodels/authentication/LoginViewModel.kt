package com.tu.health.viewmodels.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = Channel<AuthUiEvent>(Channel.BUFFERED)
    val events: Flow<AuthUiEvent> = _events.receiveAsFlow()

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }
    fun onNewPasswordChange(value: String) = _uiState.update { it.copy(newPassword = value) }

    fun login(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val email = uiState.value.email.trim()
            val password = uiState.value.password

            if (email.isBlank() || password.isBlank()) {
                emitMessage("Please enter email and password")
                return@launch
            }

            setLoading(true)

            val result = authRepository.loginUser(email = email, password = password)

            setLoading(false)

            result.onSuccess {
                onSuccess()
            }.onFailure { e ->
                emitMessage(mapLoginError(e))
            }
        }
    }

    fun logout(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            setLoading(true)
            authRepository.logout()
            _uiState.value = LoginUiState()
            onComplete()
        }
    }

    fun changePassword(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            val oldPass = uiState.value.password
            val newPass = uiState.value.newPassword

            if (oldPass.isBlank() || newPass.isBlank()) {
                emitMessage("Please fill both password fields")
                return@launch
            }

            setLoading(true)
            authRepository.changePassword(oldPassword = oldPass, newPassword = newPass)
                .onSuccess {
                    emitMessage("Password changed")
                    onDone()
                }
                .onFailure { emitMessage(it.localizedMessage ?: "Failed to change password") }
            setLoading(false)
        }
    }

    fun deleteUser(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            setLoading(true)
            authRepository.delete()
                .onSuccess {
                    _uiState.value = LoginUiState()
                    emitMessage("Account deleted")
                    onSuccess()
                }
                .onFailure { emitMessage(it.localizedMessage ?: "Failed to delete account") }
            setLoading(false)
        }
    }

    private fun setLoading(value: Boolean) = _uiState.update { it.copy(isLoading = value) }

    private suspend fun emitMessage(message: String) {
        _events.send(AuthUiEvent.ShowMessage(message))
    }

    private fun mapLoginError(e: Throwable): String {
        val raw = try {
            if (e is HttpException) {
                e.response()?.errorBody()?.string() ?: e.localizedMessage ?: "Unknown error"
            } else {
                e.localizedMessage ?: "Unknown error"
            }
        } catch (_: Exception) {
            e.localizedMessage ?: "Unknown error"
        }

        return when {
            raw.contains("no active account", ignoreCase = true) ->
                "No active account found with these credentials."
            raw.contains("invalid", ignoreCase = true) || raw.contains("credentials", ignoreCase = true) ->
                "Invalid email or password. Please try again."
            raw.contains("timeout", ignoreCase = true) ->
                "Server timeout. Please try again later."
            raw.contains("failed to connect", ignoreCase = true) ->
                "Unable to reach the server. Please check your internet connection."
            else ->
                "Login failed. Please verify your information and try again."
        }
    }
}
