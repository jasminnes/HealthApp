package com.tu.health.viewmodels

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.local.SecureTokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val secureTokenStore: SecureTokenStore
) : ViewModel() {

    private val _showAuth = MutableStateFlow(true)
    val showAuth: StateFlow<Boolean> = _showAuth.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            secureTokenStore.accessToken.collect { token ->
                _showAuth.value = token.isNullOrBlank() || isJwtExpired(token)
                _isLoading.value = false
            }
        }
    }
}

private fun isJwtExpired(token: String, clockSkewSeconds: Long = 30): Boolean {
    return try {
        val parts = token.split(".")
        if (parts.size < 2) return true

        val payloadB64 = parts[1]
        val decodedBytes = Base64.decode(
            payloadB64,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
        val payloadJson = String(decodedBytes, Charsets.UTF_8)

        val exp = JSONObject(payloadJson).optLong("exp", 0L) // seconds
        if (exp <= 0L) return true

        val nowSeconds = System.currentTimeMillis() / 1000L
        (nowSeconds + clockSkewSeconds) >= exp
    } catch (_: Exception) {
        true
    }
}
