package com.hogumiwarts.data.mapper

import com.hogumiwarts.data.entity.remote.Response.airpurifier.GetAirpurifierResponse
import com.hogumiwarts.data.entity.remote.Response.audio.AudioPowerResponse
import com.hogumiwarts.data.entity.remote.Response.audio.AudioVolumeResponse
import com.hogumiwarts.data.entity.remote.Response.audio.GetAudioStatusResponse
import com.hogumiwarts.domain.model.airpurifier.AirpurifierData
import com.hogumiwarts.domain.model.audio.AudioPowerData
import com.hogumiwarts.domain.model.audio.AudioStatusData
import com.hogumiwarts.domain.model.audio.AudioVolumeData

object AudioMapper {

    // 🔄 단일 DTO 변환 함수: DTO → 도메인 모델
    fun fromAudioStatusDataResponse(response: GetAudioStatusResponse): AudioStatusData {
        return AudioStatusData(
            tagNumber= response.tagNumber,
            deviceId = response.deviceId,
            deviceImg = response.deviceImg,
            deviceName = response.deviceName,
            manufacturerCode = response.manufacturerCode,
            deviceModel = response.deviceModel,
            deviceType = response.deviceType,
            activated = response.activated,
            audioImg = response.audioImg,
            audioName = response.audioName,
            audioArtist = response.audioArtist,
            audioVolume = response.audioVolume,
        )

    }

    fun fromAudioPowerDataResponse(response: AudioPowerResponse): AudioPowerData {
        return AudioPowerData(

            activated = response.activated,
            tagNumber = response.tagNumber,
            deviceId = response.deviceId,
            deviceImg = response.deviceImg,
            deviceName = response.deviceName,
        )

    }

    fun fromAudioVolumeDataResponse(response: AudioVolumeResponse): AudioVolumeData {
        return AudioVolumeData(
            success = response.success,
            volume = response.volume
        )

    }
}