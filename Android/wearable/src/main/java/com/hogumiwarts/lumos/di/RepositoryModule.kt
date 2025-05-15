package com.hogumiwarts.lumos.di

import com.hogumiwarts.lumos.data.repostitory.DeviceRepositoryImpl
import com.hogumiwarts.lumos.data.repostitory.LightRepositoryImpl
import com.hogumiwarts.lumos.data.repostitory.SwitchRepositoryImpl
import com.hogumiwarts.lumos.domain.repository.DeviceRepository
import com.hogumiwarts.lumos.domain.repository.LightRepository
import com.hogumiwarts.lumos.domain.repository.SwitchRepository
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
    abstract fun bindDeviceRepository(
        impl: DeviceRepositoryImpl
    ): DeviceRepository

    @Binds
    @Singleton
    abstract fun bindSwitchRepository(
        impl: SwitchRepositoryImpl
    ): SwitchRepository

    @Binds
    @Singleton
    abstract fun bindLightRepository(
        impl: LightRepositoryImpl
    ): LightRepository



}