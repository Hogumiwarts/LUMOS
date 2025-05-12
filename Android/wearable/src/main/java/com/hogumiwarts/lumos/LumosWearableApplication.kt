package com.hogumiwarts.lumos

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LumosWearableApplication  : Application(){
    override fun onCreate() {
        super.onCreate()
    }
}