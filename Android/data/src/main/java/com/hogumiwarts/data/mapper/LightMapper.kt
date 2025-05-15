package com.hogumiwarts.data.mapper

import com.hogumiwarts.data.entity.remote.Response.PatchControlResponse
import com.hogumiwarts.data.entity.remote.Response.light.GetLightStatusResponse
import com.hogumiwarts.domain.model.ControlData
import com.hogumiwarts.domain.model.light.LightStatusData


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

    fun fromSwitchPowerResponse(dtoList: PatchControlResponse): ControlData {
        return ControlData(
            success = dtoList.success
        )
    }
}