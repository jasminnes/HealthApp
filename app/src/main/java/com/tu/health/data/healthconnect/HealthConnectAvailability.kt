package com.tu.health.data.healthconnect

import android.content.Context
import androidx.health.connect.client.HealthConnectClient

enum class HcAvailability {
    Available,
    NotSupported,
    ProviderUpdateRequired
}

fun getHealthConnectAvailability(context: Context): HcAvailability {
    val status = HealthConnectClient.getSdkStatus(context)

    return when (status) {
        HealthConnectClient.SDK_UNAVAILABLE ->
            HcAvailability.NotSupported

        HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED ->
            HcAvailability.ProviderUpdateRequired

        else ->
            HcAvailability.Available
    }
}
