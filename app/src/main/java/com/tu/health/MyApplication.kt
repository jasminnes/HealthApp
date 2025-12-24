package com.tu.health

import android.app.Application
import com.tu.health.data.TokenRefreshManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), CoroutineScope by MainScope() {

    @Inject lateinit var tokenRefreshManager: TokenRefreshManager

    override fun onCreate() {
        super.onCreate()
        tokenRefreshManager.start(this)
    }
}
