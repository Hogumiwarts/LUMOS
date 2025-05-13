package com.hogumiwarts.lumos.ui.screens.auth.smartthings

import androidx.lifecycle.ViewModel
import com.hogumiwarts.data.source.remote.SmartThingsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartThingsViewModel @Inject constructor(
    private val smartThingsApi: SmartThingsApi
) : ViewModel() {

    suspend fun getAuthUrl(): String {
        return smartThingsApi.getSmartThingsAuthUrl().url
    }
}
