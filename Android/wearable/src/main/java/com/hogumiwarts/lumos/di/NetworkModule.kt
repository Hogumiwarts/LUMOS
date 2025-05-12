package com.hogumiwarts.lumos.di

import android.util.Log
import com.hogumiwarts.lumos.data.source.remote.DevicesApi
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
class NetworkModule {

    // 🔹 OkHttpClient 제공 함수
    @Provides
    @Singleton
    fun provideOkHttpClient(
        addAuthInterceptor: AddAuthInterceptor // 인증 헤더 추가 인터셉터
    ): OkHttpClient {
        // 📘 로그 출력을 위한 HttpLoggingInterceptor 구성
        val logging = HttpLoggingInterceptor { message ->
            Log.i("Post", "log: message $message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY // 전체 요청/응답 로그 출력
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃
            .readTimeout(30, TimeUnit.SECONDS)    // 읽기 타임아웃
            .writeTimeout(30, TimeUnit.SECONDS)   // 쓰기 타임아웃
            .addInterceptor(logging)              // 로그 인터셉터 추가
            .addInterceptor(addAuthInterceptor)   // 인증 인터셉터 추가
            .build()
    }

    // 🔹 Retrofit 제공 함수
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        @Named("AUTH_BASE_URL") baseUrl: String // baseUrl은 따로 모듈에서 주입
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create()) // JSON 변환
        .build()

    // 🔹 Retrofit을 기반으로 한 API 인터페이스 구현체 제공
    @Provides
    @Singleton
    fun provideDevicesApi(retrofit: Retrofit): DevicesApi =
        retrofit.create(DevicesApi::class.java)
}
