package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Response.SmartThingsAuthResponse
import com.hogumiwarts.data.entity.remote.Response.TokenResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SmartThingsApi {
    @GET("url")
    suspend fun getSmartThingsAuthUrl(): SmartThingsAuthResponse

    @GET("callback")
    suspend fun exchangeCodeForToken(@Query("code") code: String): TokenResponse

}
