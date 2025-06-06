package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Request.AudioVolumeRequest
import com.hogumiwarts.data.entity.remote.Request.PowerRequest
import com.hogumiwarts.data.entity.remote.Response.BaseResponse
import com.hogumiwarts.data.entity.remote.Response.PatchControlResponse
import com.hogumiwarts.data.entity.remote.Response.airpurifier.GetAirpurifierResponse
import com.hogumiwarts.data.entity.remote.Response.audio.AudioPowerResponse
import com.hogumiwarts.data.entity.remote.Response.audio.AudioVolumeResponse
import com.hogumiwarts.data.entity.remote.Response.audio.GetAudioStatusResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface AudioApi {

    @GET("/device/api/devices/{deviceId}/audio/status")
    suspend fun getAudioStatus(@Path("deviceId") deviceId: Long): BaseResponse<GetAudioStatusResponse>

    @PATCH("/device/api/devices/{deviceId}/audio/playback")
    suspend fun patchAudioPower(@Path("deviceId") deviceId: Long, @Body request: PowerRequest): BaseResponse<AudioPowerResponse>

    @PATCH("/device/api/devices/{deviceId}/audio/volume")
    suspend fun patchAudioVolume(@Path("deviceId") deviceId: Long, @Body request: AudioVolumeRequest): BaseResponse<AudioVolumeResponse>
}