package com.hogumiwarts.lumos.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import com.hogumiwarts.lumos.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object BaseUrlModule {

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token_prefs")
}
