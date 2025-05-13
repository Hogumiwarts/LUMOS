package com.hogumiwarts.lumos.ui.screens.auth.smartthings

import com.hogumiwarts.data.source.remote.RetrofitUtil.smartThingsApiService
import com.hogumiwarts.data.source.remote.SmartThingsApi

suspend fun getSmartThingsAuthUrl(): String {
    val response = smartThingsApiService.getSmartThingsAuthUrl()
    return response.url
}