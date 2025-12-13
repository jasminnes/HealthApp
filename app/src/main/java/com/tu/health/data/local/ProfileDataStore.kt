package com.tu.health.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        val BIRTHDATE = stringPreferencesKey("birthdate")
        val GENDER = stringPreferencesKey("gender")
    }

    val profileFlow = dataStore.data.map {
        UserProfileLocal(
            birthdate = it[BIRTHDATE],
            gender = it[GENDER]
        )
    }

    suspend fun saveBirthdate(value: String) {
        dataStore.edit { it[BIRTHDATE] = value }
    }

    suspend fun saveGender(value: String) {
        dataStore.edit { it[GENDER] = value }
    }
}

data class UserProfileLocal(
    val birthdate: String?,
    val gender: String?
)
