package com.hogumiwarts.domain.usecase

import com.hogumiwarts.domain.model.devices.GetDevicesResult
import com.hogumiwarts.domain.repository.DeviceRepository
import javax.inject.Inject

class DeviceUseCase@Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    suspend fun getDevice(): GetDevicesResult {

        val data = deviceRepository.getDevices()
        return data
    }

}