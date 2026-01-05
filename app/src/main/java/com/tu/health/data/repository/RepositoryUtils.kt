package com.tu.health.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Runs [block] on IO dispatcher and wraps the result in Kotlin Result.
 * Keeps repositories clean and consistent.
 */
internal suspend inline fun <T> safeCall(crossinline block: suspend () -> T): Result<T> {
    return try {
        val data = withContext(Dispatchers.IO) { block() }
        Result.success(data)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

/**
 * Same as safeCall, but for endpoints returning Unit (delete).
 */
internal suspend inline fun safeCallUnit(crossinline block: suspend () -> Unit): Result<Unit> {
    return safeCall { block(); Unit }
}
