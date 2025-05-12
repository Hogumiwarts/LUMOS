package com.hogumiwarts.lumos.domain.usecase

import android.util.Log
import com.hogumiwarts.lumos.domain.model.GetDevicesResult
import com.hogumiwarts.lumos.domain.model.GetSwitchStatusResult
import com.hogumiwarts.lumos.domain.repository.DeviceRepository
import com.hogumiwarts.lumos.domain.repository.SwitchRepository
import javax.inject.Inject

class SwitchUseCase@Inject constructor(
    private val switchRepository: SwitchRepository
) {
    suspend fun getSwitchStatus(deviceId: Long): GetSwitchStatusResult {

        val data = switchRepository.getSwitchStatus(deviceId)
        Log.d("TAG", "getSwitchStatus: $data")
        return data
    }

}