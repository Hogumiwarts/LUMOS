package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Response.DeviceDiscoverResponse
import com.hogumiwarts.data.entity.remote.Response.DeviceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface DeviceApi {
    @GET("devices")
    suspend fun getMyDevices(
        @Header("Authorization") token: String

    ): DeviceResponse

    @GET("discover")
    suspend fun discover(
        @Header("Authorization") token: String,
        @Query("installedAppId") installedAppId : String
    ): DeviceDiscoverResponse
}

