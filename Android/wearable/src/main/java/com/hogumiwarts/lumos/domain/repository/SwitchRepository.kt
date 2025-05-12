package com.hogumiwarts.lumos.domain.repository

import com.hogumiwarts.lumos.domain.model.GetDevicesResult
import com.hogumiwarts.lumos.domain.model.GetSwitchStatusResult

interface SwitchRepository {

    suspend fun getSwitchStatus(deviceId: Long): GetSwitchStatusResult

}