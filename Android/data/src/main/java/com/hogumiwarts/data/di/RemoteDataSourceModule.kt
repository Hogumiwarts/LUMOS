package com.hogumiwarts.data.di

import com.hogumiwarts.data.repository.weather.WeatherRemoteDataSource
import com.hogumiwarts.data.repository.weather.WeatherRemoteDataSourceImpl
import com.hogumiwarts.data.source.remote.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataSourceModule {

    @Provides
    @Singleton
    fun provideWeatherRemoteDataSource(weatherApi: WeatherApi): WeatherRemoteDataSource {
        return WeatherRemoteDataSourceImpl(weatherApi)
    }
}