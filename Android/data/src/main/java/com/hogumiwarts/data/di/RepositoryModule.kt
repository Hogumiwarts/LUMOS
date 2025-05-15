package com.hogumiwarts.data.di

import com.hogumiwarts.data.repository.AuthRepositoryImpl
import com.hogumiwarts.data.repository.weather.WeatherRepositoryImpl
import com.hogumiwarts.domain.repository.AuthRepository
import com.hogumiwarts.domain.repository.WeatherRepository
import com.hogumiwarts.data.repository.GestureRepositoryImpl
import com.hogumiwarts.data.repository.AirpurifierRepositoryImpl
import com.hogumiwarts.data.repository.AudioRepositoryImpl
import com.hogumiwarts.domain.repository.AirpurifierRepository
import com.hogumiwarts.domain.repository.AudioRepository
import com.hogumiwarts.domain.repository.GestureRepository
import dagger.Binds
import dagger.Module
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

    @Binds
    @Singleton
    abstract fun bindAudioRepository(
        impl: AudioRepositoryImpl
    ): AudioRepository


}