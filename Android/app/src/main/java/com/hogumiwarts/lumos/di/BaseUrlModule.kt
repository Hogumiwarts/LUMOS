package com.hogumiwarts.lumos.di

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

    @Provides
    @Singleton
    @Named("AUTH_BASE_URL")
    fun provideAuthBaseUrl(): String = BuildConfig.AUTH_BASE_URL


}
