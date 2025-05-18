package com.hogumiwarts.domain.usecase


import com.hogumiwarts.domain.model.PatchSwitchPowerResult
import com.hogumiwarts.domain.model.minibig.GetSwitchStatusResult
import com.hogumiwarts.domain.repository.SwitchRepository
import javax.inject.Inject

class SwitchUseCase@Inject constructor(
    private val switchRepository: SwitchRepository
) {
    suspend fun getSwitchStatus(deviceId: Long): GetSwitchStatusResult {

        val data = switchRepository.getSwitchStatus(deviceId)
        return data
    }

    suspend fun patchSwitchStatus(deviceId: Long, activated: Boolean): PatchSwitchPowerResult {

        val data = switchRepository.patchSwitchPower(deviceId =deviceId,activated = activated)
        return data
    }

}