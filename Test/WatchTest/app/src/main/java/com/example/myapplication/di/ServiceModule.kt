package com.example.myapplication.di

import com.example.myapplication.data.api.TestApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Singleton
    @Provides
    fun providesTestService(retrofit: Retrofit) : TestApiService = retrofit.create(TestApiService::class.java)

}