package com.tu.health.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        val EMAIL = stringPreferencesKey("email")
        val FIRST_NAME = stringPreferencesKey("first_name")
        val LAST_NAME = stringPreferencesKey("last_name")
        val LAST_HEALTH_UPLOAD_DAY = stringPreferencesKey("last_health_upload_day")
    }

    val profileFlow: Flow<UserProfileLocal> = dataStore.data
        .map { prefs ->
            UserProfileLocal(
                email = prefs[EMAIL] ?: "",
                firstName = prefs[FIRST_NAME] ?: "",
                lastName = prefs[LAST_NAME] ?: ""
            )
        }

    val lastHealthUploadDayFlow: Flow<String?> = dataStore.data
        .map { prefs -> prefs[LAST_HEALTH_UPLOAD_DAY] }

    suspend fun saveEmail(value: String) = dataStore.edit { it[EMAIL] = value }
    suspend fun saveFirstName(value: String) = dataStore.edit { it[FIRST_NAME] = value }
    suspend fun saveLastName(value: String) = dataStore.edit { it[LAST_NAME] = value }
    suspend fun saveLastHealthUploadDay(value: String) = dataStore.edit { it[LAST_HEALTH_UPLOAD_DAY] = value }

    suspend fun clear() = dataStore.edit {
        it.clear()
    }
}

data class UserProfileLocal(
    val email: String,
    val firstName: String,
    val lastName: String
)
