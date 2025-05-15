package com.hogumiwarts.data.di

import android.util.Log
import com.hogumiwarts.data.BuildConfig
import com.hogumiwarts.data.repository.MemberRepositoryImpl
import com.hogumiwarts.data.source.remote.AirpurifierApi
import com.hogumiwarts.data.source.remote.AudioApi
import com.hogumiwarts.data.source.remote.AuthApi
import com.hogumiwarts.data.source.remote.DeviceApi
import com.hogumiwarts.data.source.remote.WeatherApi
import com.hogumiwarts.data.source.remote.GestureApi
import com.hogumiwarts.data.source.remote.MemberApi
import com.hogumiwarts.data.source.remote.RoutineApi
import com.hogumiwarts.data.source.remote.SmartThingsApi
import com.hogumiwarts.domain.repository.MemberRepository
import dagger.Binds
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
    fun provideOkHttpClient(
        addAuthInterceptor: AddAuthInterceptor
    ): OkHttpClient {
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
            .addInterceptor(addAuthInterceptor)
            .build()
    }


    @Provides
    @Singleton
    @Named("AUTH_BASE_URL")
    fun providesBaseUrl(): String = BuildConfig.AUTH_BASE_URL

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
    fun provideDeviceApi(@Named("smartThingsRetrofit") retrofit: Retrofit): SmartThingsApi =
        retrofit.create(SmartThingsApi::class.java)

    @Provides
    @Singleton
    @Named("BASE_URL")
    fun provideBaseUrl(): String = BuildConfig.BASE_URL


    @Provides
    @Singleton
    @Named("BaseRetrofit")
    fun provideBaseRetrofit(
        okHttpClient: OkHttpClient,
        @Named("DEVICE_BASE_URL") baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // 디바이스 관련
    @Provides
    @Singleton
    @Named("DEVICE_BASE_URL")
    fun provideDeviceApiBaseUrl(): String = BuildConfig.DEVICE_BASE_URL

    @Provides
    @Singleton
    @Named("deviceRetrofit")
    fun provideDeviceApiRetrofit(
        okHttpClient: OkHttpClient,
        @Named("DEVICE_BASE_URL") baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideAripurifierApi(@Named("BaseRetrofit") retrofit: Retrofit): AirpurifierApi =
        retrofit.create(AirpurifierApi::class.java)

    @Provides
    @Singleton
    fun provideAudioApi(@Named("BaseRetrofit") retrofit: Retrofit): AudioApi =
        retrofit.create(AudioApi::class.java)

    @Provides
    @Singleton
    fun provideDevicedListApi(@Named("deviceRetrofit") retrofit: Retrofit): DeviceApi =
        retrofit.create(DeviceApi::class.java)

    @Provides
    @Singleton
    @Named("memberRetrofit")
    fun provideMemberRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun memberApi(@Named("BaseRetrofit") retrofit: Retrofit): MemberApi =
        retrofit.create(MemberApi::class.java)


    @Provides
    @Singleton
    fun routineApi(@Named("BaseRetrofit") retrofit: Retrofit): RoutineApi =
        retrofit.create(RoutineApi::class.java)


}