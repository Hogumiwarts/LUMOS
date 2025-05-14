package com.hogumiwarts.data.di

import com.hogumiwarts.data.repository.JwtRepositoryImpl
import com.hogumiwarts.data.source.local.JwtDataStore
import com.hogumiwarts.data.source.local.JwtLocalDataSource
import com.hogumiwarts.data.source.local.JwtLocalDataSourceImpl
import com.hogumiwarts.domain.repository.JwtRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object JwtModule {

    @Provides
    @Singleton
    fun provideJwtLocalDataSource(jwtDataStore: JwtDataStore): JwtLocalDataSource =
        JwtLocalDataSourceImpl(jwtDataStore)

    @Provides
    @Singleton
    fun provideJwtRepository(localDataSource: JwtLocalDataSource): JwtRepository =
        JwtRepositoryImpl(localDataSource)
}

