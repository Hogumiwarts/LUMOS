package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.DeviceResult
import com.hogumiwarts.domain.model.devices.GetDevicesResult

// device 관련 인터페이스
interface DeviceRepository {
    suspend fun getDevicesFromServer(accessToken: String): List<DeviceResult>
    suspend fun discoverDevices(token: String, installedAppId: String): List<DeviceResult>
    suspend fun getDevices(): GetDevicesResult
}