package com.tu.health.viewmodels.authentication

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.local.SecureTokenStore
import com.tu.health.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val secureTokenStore: SecureTokenStore
) : ViewModel() {

    // UI states
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> get() = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> get() = _password

    private val _repeatPassword = MutableStateFlow("")
    val repeatPassword: StateFlow<String> get() = _repeatPassword

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> get() = _firstName

    private val _lastName = MutableStateFlow<String?>(null)
    val lastName: StateFlow<String?> get() = _lastName

    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String> get() = _gender

    private val _birthDate = MutableStateFlow("")
    val birthDate: StateFlow<String> get() = _birthDate

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    // Tokens from SecureTokenStore
    val accessToken = secureTokenStore.accessToken.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        null
    )

    val refreshToken = secureTokenStore.refreshToken.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        null
    )

    // Update functions
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onRepeatPasswordChange(newRepeatPassword: String) {
        _repeatPassword.value = newRepeatPassword
    }

    fun onFirstNameChange(newName: String) {
        _firstName.value = newName
    }

    fun onLastNameChange(newSurname: String) {
        _lastName.value = newSurname
    }

    fun onGenderChange(gender: String) {
        _gender.value = gender
    }

    fun onBirthDateChange(birthDate: String) {
        _birthDate.value = birthDate
    }

    fun register(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (email.value.isBlank() || password.value.isBlank() || repeatPassword.value.isBlank()) {
                onError("Please fill in all required fields")
                return@launch
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                onError("Invalid email format")
                return@launch
            }

            if (password.value != repeatPassword.value) {
                onError("Passwords do not match")
                return@launch
            }

            val passwordRegex = Regex(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])" +
                        "(?=.*[!@#\$%^&*()\\-_=+\\[\\]{}|;:'\",.<>?/`~]).{8,}\$"
            )
            if (!passwordRegex.matches(password.value)) {
                onError(
                    "Password must be at least 8 characters, contain a number, " +
                            "a lowercase letter, an uppercase letter, and a symbol"
                )
                return@launch
            }

            if (!isUserAgeValid(birthDate.value)) {
                onError("You must be at least 18 years old to register")
                return@launch
            }


            _isLoading.value = true

            try {
                val registerResult = authRepository.registerUser(
                    email = email.value,
                    password = password.value,
                    repeatPassword = repeatPassword.value,
                    firstName = firstName.value,
                    lastName = lastName.value ?: "",
                    gender = gender.value,
                    birthDate = birthDate.value
                )

                if (registerResult.isSuccess) {
                    val loginResult = authRepository.loginUser(
                        email = email.value,
                        password = password.value
                    )

                    if (loginResult.isSuccess) {
                        loginResult.getOrThrow().access.let { secureTokenStore.saveAccessToken(it) }
                        loginResult.getOrThrow().refresh.let { secureTokenStore.saveRefreshToken(it) }

                        _isLoading.value = false
                        onSuccess()
                    } else {
                        _isLoading.value = false
                        onError("Registered successfully but automatic login failed. Please try logging in.")
                    }
                } else {
                    _isLoading.value = false
                    onError(
                        registerResult.exceptionOrNull()?.localizedMessage ?: "Registration failed"
                    )
                }
            } catch (e: Exception) {
                _isLoading.value = false
                onError(e.localizedMessage ?: "Registration failed")
            }
        }
    }

    private fun isUserAgeValid(birthDateString: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val birthDate = dateFormat.parse(birthDateString) ?: return false
            val today = Calendar.getInstance()
            val birthCalendar = Calendar.getInstance().apply {
                time = birthDate
            }

            var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            age >= 18
        } catch (_: Exception) {
            false
        }
    }
}
