package com.hogumiwarts.domain.model.devices

import com.hogumiwarts.domain.model.CommonError

sealed class GetDevicesResult{
    data class Success(
        val data : List<DeviceListData>
    ):GetDevicesResult()

    data class Error(val error: CommonError) : GetDevicesResult()
}
