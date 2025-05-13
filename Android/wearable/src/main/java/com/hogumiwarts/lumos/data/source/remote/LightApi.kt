package com.hogumiwarts.lumos.data.source.remote

import com.hogumiwarts.lumos.data.entity.remote.BaseResponse
import com.hogumiwarts.lumos.data.entity.remote.PatchControlResponse
import com.hogumiwarts.lumos.data.entity.remote.PowerRequest
import com.hogumiwarts.lumos.data.entity.remote.light.GetLightStatusResponse
import com.hogumiwarts.lumos.data.entity.remote.light.PatchLightBrightRequest
import com.hogumiwarts.lumos.data.entity.remote.light.PatchLightColorRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface LightApi {

    @GET("/device/api/devices/{deviceId}/light/status")
    suspend fun getLightStatus(@Path("deviceId") deviceId: Long): BaseResponse<GetLightStatusResponse>

    @PATCH("/device/api/devices/{deviceId}/light/power")
    suspend fun patchLightPower(@Path("deviceId") deviceId: Long, @Body request: PowerRequest): BaseResponse<PatchControlResponse>

    @PATCH("/device/api/devices/{deviceId}/light/bright")
    suspend fun patchLightBright(@Path("deviceId") deviceId: Long, @Body request: PatchLightBrightRequest): BaseResponse<PatchControlResponse>

    @PATCH("/device/api/devices/{deviceId}/light/color")
    suspend fun patchLightColor(@Path("deviceId") deviceId: Long, @Body request: PatchLightColorRequest): BaseResponse<PatchControlResponse>


}