package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Response.SmartThingsAuthResponse
import retrofit2.http.GET

interface SmartThingsApi {
    @GET("url")
    suspend fun getSmartThingsAuthUrl(): SmartThingsAuthResponse
}
