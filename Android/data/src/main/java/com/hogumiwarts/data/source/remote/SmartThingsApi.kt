package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Response.DeviceStatusResponse
import com.hogumiwarts.data.entity.remote.Response.SmartThingsAuthResponse
import com.hogumiwarts.data.entity.remote.Response.SmartThingsDeviceListResponse
import com.hogumiwarts.data.entity.remote.Response.TokenResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SmartThingsApi {
    @GET("oauth/url")
    suspend fun getSmartThingsAuthUrl(): SmartThingsAuthResponse

    @GET("devices")
    suspend fun getDeviceList(
        @Header("installedappid") installedAppId: String
    ): SmartThingsDeviceListResponse

    @GET("devices/{deviceId}/status")
    suspend fun getDeviceStatus(
        @Path("deviceId") deviceId: String,
        @Header("installedappid") installedappid: String
    ): DeviceStatusResponse
}
