package com.hogumiwarts.lumos.ui.screens.control

import android.content.Context
import androidx.core.uwb.UwbManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UwbModule {
    @Provides
    @Singleton
    fun provideUwbManager(@ApplicationContext context: Context): UwbManager {
        return UwbManager.createInstance(context)
    }

    @Provides
    @Singleton
    fun provideUwbRanging(uwbManager: UwbManager): UwbRanging {
        return UwbRanging(uwbManager)
    }
}