package com.hogumiwarts.lumos.domain.repository

import com.hogumiwarts.lumos.domain.model.GetDevicesResult

interface DeviceRepository {
    suspend fun getDevices(): GetDevicesResult
}