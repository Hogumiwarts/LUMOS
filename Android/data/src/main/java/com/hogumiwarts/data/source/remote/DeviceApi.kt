package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Response.DeviceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface DeviceApi {
    @GET("devices")
    suspend fun getMyDevices(
        @Header("Authorization") token: String

    ): DeviceResponse
}
