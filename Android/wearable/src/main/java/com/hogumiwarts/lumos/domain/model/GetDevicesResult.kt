package com.hogumiwarts.lumos.domain.model

sealed class GetDevicesResult{
    data class Success(
        val data : List<DeviceListData>
    ):GetDevicesResult()

    data class Error(val error: CommonError) : GetDevicesResult()
}
