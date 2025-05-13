package com.hogumiwarts.lumos.data.entity.mapper

import com.hogumiwarts.lumos.data.entity.remote.GetSwitchStatusResponse
import com.hogumiwarts.lumos.domain.model.SwitchStatusData

object SwitchMapper {

    // 🔄 전체 리스트 변환 함수: DTO 리스트 → 도메인 모델 리스트
    fun fromSwitchStatusDataResponse(dtoList: GetSwitchStatusResponse): SwitchStatusData {
        return SwitchStatusData(
            tagNumber = dtoList.tagNumber,
            deviceId = dtoList.deviceId,
            deviceImg = dtoList.deviceImg,
            deviceName = dtoList.deviceName,
            deviceManufacturer = dtoList.deviceManufacturer,
            deviceModel = dtoList.deviceModel,
            deviceType = dtoList.deviceType,
            activated = dtoList.activated

        )
    }



}