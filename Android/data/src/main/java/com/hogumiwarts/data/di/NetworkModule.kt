package com.hogumiwarts.data.di

import com.hogumiwarts.data.source.remote.AuthApi
import com.hogumiwarts.data.source.remote.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        @Named("AUTH_BASE_URL") baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    // 날씨 API 관련 코드 추가
    @Provides
    @Singleton
    @Named("WEATHER_BASE_URL")
    fun provideWeatherBaseUrl(): String = "https://api.openweathermap.org/data/2.5/"

    @Provides
    @Singleton
    @Named("weatherRetrofit")
    fun provideWeatherRetrofit(
        okHttpClient: OkHttpClient,
        @Named("WEATHER_BASE_URL") baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideWeatherApi(@Named("weatherRetrofit") retrofit: Retrofit): WeatherApi =
        retrofit.create(WeatherApi::class.java)

}