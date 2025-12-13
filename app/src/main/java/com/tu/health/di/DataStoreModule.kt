package com.tu.health.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val PROFILE_DATASTORE = "profile_preferences"

val Context.profileDataStore by preferencesDataStore(PROFILE_DATASTORE)

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideProfileDataStore(context: Context) = context.profileDataStore
}
