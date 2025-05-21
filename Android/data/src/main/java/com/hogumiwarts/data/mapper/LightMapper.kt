package com.hogumiwarts.data.mapper

import com.hogumiwarts.data.entity.remote.Request.LightTemperatureRequest
import com.hogumiwarts.data.entity.remote.Response.PatchControlResponse
import com.hogumiwarts.data.entity.remote.Response.light.GetLightStatusResponse
import com.hogumiwarts.data.entity.remote.Response.light.LightBrightResponse
import com.hogumiwarts.data.entity.remote.Response.light.LightColorResponse
import com.hogumiwarts.data.entity.remote.Response.light.LightTemperatureResponse
import com.hogumiwarts.domain.model.ControlData
import com.hogumiwarts.domain.model.light.LightBrightData
import com.hogumiwarts.domain.model.light.LightBrightResult
import com.hogumiwarts.domain.model.light.LightColorData
import com.hogumiwarts.domain.model.light.LightStatusData
import com.hogumiwarts.domain.model.light.LightTemperatureData


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
            success = dtoList.success,
                    activated= dtoList.activated
        )
    }

    fun fromLightBrightResponse(dtoList: LightBrightResponse): LightBrightData {
        return LightBrightData(
            success = dtoList.success,
            brightness= dtoList.brightness
        )
    }

    fun fromLightColorResponse(dtoList: LightColorResponse): LightColorData {
        return LightColorData(
            success = dtoList.success,
            hue= dtoList.hue,
            saturation= dtoList.saturation,
        )
    }

    fun fromLightTemperatureResponse(dtoList: LightTemperatureResponse): LightTemperatureData {
        return LightTemperatureData(
            success = dtoList.success,
            temperature= dtoList.temperature
        )
    }
}