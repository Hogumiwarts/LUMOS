package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.DeviceResult

// device 관련 인터페이스
interface DeviceRepository {
    suspend fun getDevicesFromServer(accessToken: String): List<DeviceResult>
}