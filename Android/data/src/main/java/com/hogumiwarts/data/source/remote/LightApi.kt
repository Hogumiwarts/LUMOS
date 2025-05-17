package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Request.LightTemperatureRequest
import com.hogumiwarts.data.entity.remote.Request.PowerRequest
import com.hogumiwarts.data.entity.remote.Response.BaseResponse
import com.hogumiwarts.data.entity.remote.Response.PatchControlResponse
import com.hogumiwarts.data.entity.remote.Response.light.GetLightStatusResponse
import com.hogumiwarts.data.entity.remote.Response.light.LightBrightResponse
import com.hogumiwarts.data.entity.remote.Response.light.LightColorResponse
import com.hogumiwarts.data.entity.remote.Response.light.LightTemperatureResponse
import com.hogumiwarts.data.entity.remote.Response.light.PatchLightBrightRequest
import com.hogumiwarts.data.entity.remote.Response.light.PatchLightColorRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface LightApi {

    @GET("/device/api/devices/{deviceId}/light/status")
    suspend fun getLightStatus(@Path("deviceId") deviceId: Int): BaseResponse<GetLightStatusResponse>

    @PATCH("/device/api/devices/{deviceId}/light/power")
    suspend fun patchLightPower(@Path("deviceId") deviceId: Int, @Body request: PowerRequest): BaseResponse<PatchControlResponse>

    @PATCH("/device/api/devices/{deviceId}/light/bright")
    suspend fun patchLightBright(@Path("deviceId") deviceId: Long, @Body request: PatchLightBrightRequest): BaseResponse<LightBrightResponse>

    @PATCH("/device/api/devices/{deviceId}/light/color")
    suspend fun patchLightColor(@Path("deviceId") deviceId: Long, @Body request: PatchLightColorRequest): BaseResponse<LightColorResponse>

    @PATCH("/device/api/devices/{deviceId}/light/temperature")
    suspend fun patchLightTemperature(@Path("deviceId") deviceId: Long, @Body request: LightTemperatureRequest): BaseResponse<LightTemperatureResponse>


}