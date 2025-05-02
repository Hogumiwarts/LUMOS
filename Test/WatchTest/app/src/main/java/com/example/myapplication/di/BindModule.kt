package com.example.myapplication.di

import com.example.myapplication.data.repository.TestRepositoryImpl
import com.example.myapplication.domain.repository.TestRepository
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
    abstract fun bindTestRepository(
        impl: TestRepositoryImpl
    ): TestRepository
}