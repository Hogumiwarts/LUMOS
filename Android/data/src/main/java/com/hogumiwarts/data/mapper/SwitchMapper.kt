package com.hogumiwarts.data.mapper

import com.hogumiwarts.data.entity.remote.Response.minibig.GetSwitchStatusResponse
import com.hogumiwarts.domain.model.minibig.SwitchStatusData


object SwitchMapper {

    // 🔄 전체 리스트 변환 함수: DTO 리스트 → 도메인 모델 리스트
    fun fromSwitchStatusDataResponse(dtoList: GetSwitchStatusResponse): SwitchStatusData {
        return SwitchStatusData(
            tagNumber = dtoList.tagNumber,
            deviceId = dtoList.deviceId,
            deviceImg = dtoList.deviceImg,
            deviceName = dtoList.deviceName,
            manufacturerCode = dtoList.manufacturerCode,
            deviceModel = dtoList.deviceModel,
            deviceType = dtoList.deviceType,
            activated = dtoList.activated

        )
    }



}