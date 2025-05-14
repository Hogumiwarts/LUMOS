package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Response.BaseResponse
import com.hogumiwarts.data.entity.remote.Response.airpurifier.GetAirpurifierResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface AirpurifierApi {

    @GET("/device/api/devices/{deviceId}/airpurifier/status")
    suspend fun getAirpurifierStatus(@Path("deviceId") deviceId: Long): BaseResponse<GetAirpurifierResponse>
}