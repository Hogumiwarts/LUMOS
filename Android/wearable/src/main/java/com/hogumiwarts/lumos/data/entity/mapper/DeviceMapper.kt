package com.hogumiwarts.lumos.data.entity.mapper

import com.hogumiwarts.lumos.data.entity.remote.GetDevicesResponse
import com.hogumiwarts.lumos.domain.model.DeviceListData

// 🔹 데이터 계층의 DTO를 도메인 모델로 변환하는 Mapper
object DeviceMapper {

    // 🔄 전체 리스트 변환 함수: DTO 리스트 → 도메인 모델 리스트
    fun fromDeviceListDataResponseList(dtoList: List<GetDevicesResponse>): List<DeviceListData> {
        return dtoList.map { fromDeviceListDataResponse(it) }
    }

    // 🔄 단일 DTO 변환 함수: DTO → 도메인 모델
    fun fromDeviceListDataResponse(response: GetDevicesResponse): DeviceListData {
        return DeviceListData(
            deviceId = response.deviceId,
            tagNumber = response.tagNumber,
            installedAppId = response.installedAppId,
            deviceImg = response.deviceImg,
            deviceName = response.deviceName,
            deviceType = response.deviceType,
            activated = response.activated
        )
    }
}
