package com.tu.health.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.local.SecureTokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val secureTokenStore: SecureTokenStore
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _navigateToAuthScreen = MutableStateFlow(false)
    val navigateToAuthScreen: StateFlow<Boolean> = _navigateToAuthScreen

    init {
        observeSession()
    }

    private fun observeSession() {
        viewModelScope.launch {
            secureTokenStore.accessToken.collectLatest { token ->
                if (token.isNullOrBlank()) {
                    _isLoggedIn.value = false
                    _navigateToAuthScreen.value = true
                } else {
                    _isLoggedIn.value = true
                }
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            secureTokenStore.clear()
        }
    }
}
