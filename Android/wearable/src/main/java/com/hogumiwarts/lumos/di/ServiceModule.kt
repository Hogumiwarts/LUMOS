package com.hogumiwarts.lumos.di


import com.hogumiwarts.lumos.data.source.remote.DevicesApi
import com.hogumiwarts.lumos.data.source.remote.LightApi
import com.hogumiwarts.lumos.data.source.remote.RoutineApi
import com.hogumiwarts.lumos.data.source.remote.SwitchApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    // 🔹 Retrofit을 기반으로 한 API 인터페이스 구현체 제공
    @Provides
    @Singleton
    fun provideDevicesApi(@Named("WearableRetrofit")retrofit: Retrofit): DevicesApi =
        retrofit.create(DevicesApi::class.java)

    @Singleton
    @Provides
    fun providesSwitchService(@Named("WearableRetrofit")retrofit: Retrofit) : SwitchApi = retrofit.create(SwitchApi::class.java)

    @Singleton
    @Provides
    fun providesLightService(@Named("WearableRetrofit")retrofit: Retrofit) : LightApi = retrofit.create(LightApi::class.java)

    @Singleton
    @Provides
    fun providesRoutineService(@Named("WearableRetrofit")retrofit: Retrofit) : RoutineApi = retrofit.create(RoutineApi::class.java)
}