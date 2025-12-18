package com.tu.health.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.tu.health.data.local.ProfileDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val PROFILE_DATASTORE = "profile_preferences"

val Context.profileDataStore by preferencesDataStore(PROFILE_DATASTORE)

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideRawProfileDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(PROFILE_DATASTORE) }
        )
    }

    @Provides
    @Singleton
    fun provideProfileDataStoreWrapper(
        rawDataStore: DataStore<Preferences>
    ): ProfileDataStore {
        return ProfileDataStore(rawDataStore)
    }
}
