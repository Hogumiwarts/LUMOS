package com.hogumiwarts.data.di

import com.hogumiwarts.data.repository.AuthRepositoryImpl
import com.hogumiwarts.data.repository.weather.WeatherRepositoryImpl
import com.hogumiwarts.domain.repository.AuthRepository
import com.hogumiwarts.domain.repository.WeatherRepository
import com.hogumiwarts.data.repository.GestureRepositoryImpl
import com.hogumiwarts.data.repository.JwtRepositoryImpl
import com.hogumiwarts.data.repository.airpurifier.AirpurifierRepositoryImpl
import com.hogumiwarts.data.source.local.JwtDataStore
import com.hogumiwarts.data.source.local.JwtLocalDataSource
import com.hogumiwarts.data.source.local.JwtLocalDataSourceImpl
import com.hogumiwarts.domain.repository.AirpurifierRepository
import com.hogumiwarts.domain.repository.GestureRepository
import com.hogumiwarts.domain.repository.JwtRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        impl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindGestureRepository(
        impl: GestureRepositoryImpl
    ): GestureRepository

    @Binds
    @Singleton
    abstract fun bindAirpurifierRepository(
        impl: AirpurifierRepositoryImpl
    ): AirpurifierRepository


}