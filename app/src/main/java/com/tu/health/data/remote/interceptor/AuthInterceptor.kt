package com.tu.health.data.remote.interceptor

import com.tu.health.data.local.SecureTokenStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val secureTokenStore: SecureTokenStore
) : Interceptor {

    private val noAuthPaths = setOf(
        "/account/register/",
        "/account/login/",
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (noAuthPaths.any { path.endsWith(it) }) {
            return chain.proceed(request)
        }

        val token = runBlocking { secureTokenStore.accessToken.firstOrNull() }.orEmpty()
        if (token.isBlank()) return chain.proceed(request)

        val authed = request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authed)
    }
}
