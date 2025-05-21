package com.hogumiwarts.data.di

import com.hogumiwarts.data.repository.AuthRepositoryImpl
import com.hogumiwarts.data.repository.weather.WeatherRepositoryImpl
import com.hogumiwarts.domain.repository.AuthRepository
import com.hogumiwarts.domain.repository.WeatherRepository
import com.hogumiwarts.data.repository.GestureRepositoryImpl
import com.hogumiwarts.data.repository.AirpurifierRepositoryImpl
import com.hogumiwarts.data.repository.AudioRepositoryImpl
import com.hogumiwarts.data.repository.LightRepositoryImpl
import com.hogumiwarts.data.repository.MemberRepositoryImpl
import com.hogumiwarts.data.repository.SwitchRepositoryImpl
import com.hogumiwarts.data.repository.routine.RoutineRepositoryImpl
import com.hogumiwarts.domain.repository.AirpurifierRepository
import com.hogumiwarts.domain.repository.AudioRepository
import com.hogumiwarts.domain.repository.GestureRepository
import com.hogumiwarts.domain.repository.LightRepository
import com.hogumiwarts.domain.repository.MemberRepository
import com.hogumiwarts.domain.repository.RoutineRepository
import com.hogumiwarts.domain.repository.SwitchRepository
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

    @Binds
    @Singleton
    abstract fun bindMemberRepository(
        memberRepositoryImpl: MemberRepositoryImpl
    ): MemberRepository

    @Binds
    @Singleton
    abstract fun bindRoutineRepository(
        routineRepositoryImpl: RoutineRepositoryImpl
    ): RoutineRepository

    @Binds
    @Singleton
    abstract fun bindLightRepository(
        impl: LightRepositoryImpl
    ): LightRepository

    @Binds
    abstract fun bindSwitchRepository(
        impl: SwitchRepositoryImpl
    ): SwitchRepository
}