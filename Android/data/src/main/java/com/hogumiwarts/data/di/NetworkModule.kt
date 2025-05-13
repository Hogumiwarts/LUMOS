package com.hogumiwarts.data.di

import android.util.Log
import com.hogumiwarts.data.BuildConfig
import com.hogumiwarts.data.source.remote.AuthApi
import com.hogumiwarts.data.source.remote.WeatherApi
import com.hogumiwarts.data.source.remote.GestureApi
import com.hogumiwarts.data.source.remote.SmartThingsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor { message ->
            Log.i("Post", "log: message $message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

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

    @Provides
    @Singleton
    fun provideGestureApi(retrofit: Retrofit): GestureApi = retrofit.create(GestureApi::class.java)

    // smartThings API 등록
    @Provides
    @Singleton
    @Named("SMART_BASE_URL")
    fun provideSmartThingsBaseUrl(): String = BuildConfig.SMART_BASE_URL

    @Provides
    @Singleton
    @Named("smartThingsRetrofit")
    fun provideSmartThingsRetrofit(
        okHttpClient: OkHttpClient,
        @Named("SMART_BASE_URL") baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideSmartThingsApi(@Named("smartThingsRetrofit") retrofit: Retrofit): SmartThingsApi =
        retrofit.create(SmartThingsApi::class.java)
}