package com.tu.health.viewmodels.authentication

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _events = Channel<AuthUiEvent>(Channel.BUFFERED)
    val events: Flow<AuthUiEvent> = _events.receiveAsFlow()

    fun onEmailChange(v: String) = _uiState.update { it.copy(email = v) }
    fun onPasswordChange(v: String) = _uiState.update { it.copy(password = v) }
    fun onRepeatPasswordChange(v: String) = _uiState.update { it.copy(repeatPassword = v) }
    fun onFirstNameChange(v: String) = _uiState.update { it.copy(firstName = v) }
    fun onLastNameChange(v: String) = _uiState.update { it.copy(lastName = v) }
    fun onGenderChange(v: String) = _uiState.update { it.copy(gender = v) }
    fun onBirthDateChange(v: String) = _uiState.update { it.copy(birthDate = v) }

    fun register(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val s = uiState.value

            val validationError = validate(s)
            if (validationError != null) {
                emitMessage(validationError)
                return@launch
            }

            setLoading(true)

            val registerResult = authRepository.registerUser(
                email = s.email.trim(),
                password = s.password,
                repeatPassword = s.repeatPassword,
                firstName = s.firstName.trim(),
                lastName = s.lastName.trim(),
                gender = s.gender,
                birthDate = s.birthDate
            )

            if (registerResult.isSuccess) {
                val loginResult = authRepository.loginUser(
                    email = s.email.trim(),
                    password = s.password
                )

                if (loginResult.isSuccess) {
                    setLoading(false)
                    onSuccess()
                } else {
                    setLoading(false)
                    emitMessage("Registered successfully but automatic login failed. Please try logging in.")
                }
            } else {
                setLoading(false)
                emitMessage(registerResult.exceptionOrNull()?.localizedMessage ?: "Registration failed")
            }
        }
    }

    private fun setLoading(value: Boolean) = _uiState.update { it.copy(isLoading = value) }

    private suspend fun emitMessage(message: String) {
        _events.send(AuthUiEvent.ShowMessage(message))
    }

    private fun validate(s: SignUpUiState): String? {
        if (s.email.isBlank() || s.password.isBlank() || s.repeatPassword.isBlank() || s.firstName.isBlank()) {
            return "Please fill in all required fields"
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(s.email.trim()).matches()) {
            return "Invalid email format"
        }
        if (s.password != s.repeatPassword) {
            return "Passwords do not match"
        }
        if (!isPasswordStrong(s.password)) {
            return "Password must be at least 8 characters, contain a number, a lowercase letter, an uppercase letter, and a symbol"
        }
        if (!isUserAgeValid(s.birthDate)) {
            return "You must be at least 18 years old to register"
        }
        return null
    }

    private fun isPasswordStrong(password: String): Boolean {
        val passwordRegex = Regex(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&*()\\-_=+\\[\\]{}|;:'\",.<>?/`~]).{8,}\$"
        )
        return passwordRegex.matches(password)
    }

    private fun isUserAgeValid(birthDateString: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val birthDate = dateFormat.parse(birthDateString) ?: return false
            val today = Calendar.getInstance()
            val birthCalendar = Calendar.getInstance().apply { time = birthDate }

            var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) age--
            age >= 18
        } catch (_: Exception) {
            false
        }
    }
}
