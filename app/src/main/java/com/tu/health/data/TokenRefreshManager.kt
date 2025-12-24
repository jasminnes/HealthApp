package com.tu.health.data

import android.util.Base64.URL_SAFE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import android.util.Base64.decode
import com.tu.health.data.local.SecureTokenStore
import com.tu.health.data.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
@Singleton
class TokenRefreshManager @Inject constructor(
    private val tokenStore: SecureTokenStore,
    private val userRepository: AuthRepository
) {

    private val refreshMutex = Mutex()

    fun start(scope: CoroutineScope) {
        scope.launch {
            tokenStore.accessToken.collectLatest { token ->
                val delayMillis = token?.let {
                    getDelayBeforeRefresh(it)
                } ?: return@collectLatest

                delay(delayMillis)

                refreshMutex.withLock {
                    userRepository.refreshAccessToken()
                }
            }
        }
    }

    private fun getDelayBeforeRefresh(token: String): Long {
        val exp = getJwtExpiration(token) ?: return 0
        return (exp * 1000 - System.currentTimeMillis() - 60_000)
            .coerceAtLeast(0)
    }

    companion object {
        fun getJwtExpiration(accessToken: String): Long? {
            return try {
                val parts = accessToken.split(".")
                if (parts.size != 3) return null
                val payload = String(decode(parts[1], URL_SAFE))
                JSONObject(payload).getLong("exp")
            } catch (e: Exception) {
                null
            }
        }
    }
}
