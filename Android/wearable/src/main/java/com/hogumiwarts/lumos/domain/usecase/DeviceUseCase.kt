package com.hogumiwarts.lumos.domain.usecase

import com.hogumiwarts.lumos.domain.model.GetDevicesResult
import com.hogumiwarts.lumos.domain.repository.DeviceRepository
import javax.inject.Inject

class DeviceUseCase@Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    suspend fun getDevice(): GetDevicesResult {

        val data = deviceRepository.getDevices()
        return data
    }

}