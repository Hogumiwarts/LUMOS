package com.hogumiwarts.data.mapper

import com.hogumiwarts.data.entity.remote.Response.PatchControlResponse
import com.hogumiwarts.data.entity.remote.Response.airpurifier.GetAirpurifierResponse
import com.hogumiwarts.domain.model.ControlData
import com.hogumiwarts.domain.model.airpurifier.AirpurifierData

object AirpurifierMapper {



    // 🔄 단일 DTO 변환 함수: DTO → 도메인 모델
    fun fromAirpurifierDataResponse(response: GetAirpurifierResponse): AirpurifierData {
        return AirpurifierData(
            tagNumber= response.tagNumber,
            deviceId = response.deviceId,
            deviceImg = response.deviceImg,
            deviceName = response.deviceName,
            manufacturerCode = response.manufacturerCode,
            deviceModel = response.deviceModel,
            deviceType = response.deviceType,
            activated = response.activated,
            caqi = response.caqi,
            odorLevel = response.odorLevel,
            dustLevel = response.dustLevel,
            fineDustLevel = response.fineDustLevel,
            fanMode = response.fanMode,
            filterUsageTime = response.filterUsageTime,
        )

    }

    fun fromPowerResponse(dtoList: PatchControlResponse): ControlData {
        return ControlData(
            success = dtoList.success
        )
    }

}
