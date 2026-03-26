package com.tu.health.viewmodels.profile.onboarding

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tu.health.data.healthconnect.HcAvailability
import com.tu.health.data.healthconnect.HealthReadPermissions
import com.tu.health.data.healthconnect.getHealthConnectAvailability
import com.tu.health.data.healthconnect.dto.HealthSnapshot
import com.tu.health.data.repository.HealthConnectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.time.LocalDate
import java.time.ZoneId

sealed class HcUiState {
    data object Checking : HcUiState()
    data object NotSupported : HcUiState()
    data object NeedsProviderUpdate : HcUiState()
    data object NeedsPermission : HcUiState()

    data class Ready(
        val snapshot: HealthSnapshot
    ) : HcUiState()

    data class Error(val message: String) : HcUiState()
}

@HiltViewModel
class HealthConnectViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val repo: HealthConnectRepository
) : ViewModel() {

    private val _state = MutableStateFlow<HcUiState>(HcUiState.Checking)
    val state: StateFlow<HcUiState> = _state

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun refresh() {
        viewModelScope.launch {
            try {
                _state.value = HcUiState.Checking

                when (getHealthConnectAvailability(appContext)) {
                    HcAvailability.NotSupported -> {
                        _state.value = HcUiState.NotSupported
                        return@launch
                    }
                    HcAvailability.ProviderUpdateRequired -> {
                        _state.value = HcUiState.NeedsProviderUpdate
                        return@launch
                    }
                    HcAvailability.Available -> Unit
                }

                val hasPerms = repo.hasAllPermissions(HealthReadPermissions)
                if (!hasPerms) {
                    _state.value = HcUiState.NeedsPermission
                    return@launch
                }

                val zone = ZoneId.systemDefault()
                val today = LocalDate.now(zone)

                val snapshot = repo.readHealthSnapshotForDate(today)

                _state.value = HcUiState.Ready(snapshot)

            } catch (t: Throwable) {
                _state.value = HcUiState.Error(t.message ?: "Unknown error")
            }
        }
    }
}
