package com.hogumiwarts.lumos.data.entity.mapper

import com.hogumiwarts.lumos.data.entity.remote.GetSwitchStatusResponse
import com.hogumiwarts.lumos.data.entity.remote.light.GetLightStatusResponse
import com.hogumiwarts.lumos.domain.model.SwitchStatusData
import com.hogumiwarts.lumos.domain.model.light.LightStatusData

object LightMapper {

    fun fromSwitchStatusDataResponse(dtoList: GetLightStatusResponse): LightStatusData {
        return LightStatusData(
            tagNumber = dtoList.tagNumber,
            deviceId = dtoList.deviceId,
            deviceImg = dtoList.deviceImg,
            deviceName = dtoList.deviceName,
            manufacturerCode = dtoList.manufacturerCode,
            deviceModel = dtoList.deviceModel,
            deviceType = dtoList.deviceType,
            activated = dtoList.activated,
            brightness = dtoList.brightness,
            lightTemperature = dtoList.lightTemperature,
            hue = dtoList.hue,
            saturation = dtoList.saturation,

        )
    }
}