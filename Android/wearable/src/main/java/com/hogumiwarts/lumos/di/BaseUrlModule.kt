package com.hogumiwarts.lumos.di

import android.content.Context
import com.hogumiwarts.lumos.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BaseUrlModule {
    @Provides
    @Singleton
    @Named("WEARABLE_URL")
    fun provideAuthBaseUrl(): String = BuildConfig.AUTH_BASE_URL

}