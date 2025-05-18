package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Request.FanModeRequest
import com.hogumiwarts.data.entity.remote.Request.PowerRequest
import com.hogumiwarts.data.entity.remote.Response.BaseResponse
import com.hogumiwarts.data.entity.remote.Response.PatchControlResponse
import com.hogumiwarts.data.entity.remote.Response.airpurifier.GetAirpurifierResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface AirpurifierApi {

    @GET("/device/api/devices/{deviceId}/airpurifier/status")
    suspend fun getAirpurifierStatus(@Path("deviceId") deviceId: Int): BaseResponse<GetAirpurifierResponse>

    @PATCH("/device/api/devices/{deviceId}/airpurifier/power")
    suspend fun patchAirpurifierPower(@Path("deviceId") deviceId: Int, @Body request: PowerRequest): BaseResponse<PatchControlResponse>

    @PATCH("/device/api/devices/{deviceId}/airpurifier/fanmode")
    suspend fun patchAirpurifierFanMode(@Path("deviceId") deviceId: Int, @Body request: FanModeRequest): BaseResponse<PatchControlResponse>
}