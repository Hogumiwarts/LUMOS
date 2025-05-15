package com.hogumiwarts.data.entity.remote.Response

// 계정 인증
data class SmartThingsAuthResponse(
    val url: String
)

// 기기 목록
data class SmartThingsDeviceListResponse(
    val success: Boolean,
    val devices: List<SmartThingsDevice>
)

data class SmartThingsDevice(
    val deviceId: String,
    val name: String,
    val label: String?,
    val components: List<Component>
)

data class Component(
    val id: String,
    val label: String?,
    val capabilities: List<Capability>,
    val categories: List<Category>
)

data class Capability(
    val id: String,
    val version: Int,
    val optional: Boolean
)

data class Category(
    val name: String,
    val categoryType: String
)


// status
data class DeviceStatusResponse(
    val success: Boolean,
    val status: Status
)

data class Status(
    val components: Map<String, ComponentStatus>
)

data class ComponentStatus(
    val switch: SwitchCapability? = null,
    val healthCheck: HealthCheckCapability? = null,
    val battery: BatteryCapability? = null,
    val custom_airPurifierOperationMode: AirPurifierOperationModeCapability? = null,
    val refresh: Any? = null
)

data class AirPurifierOperationModeCapability(
    val apOperationMode: ValueWithTimestamp? = null
)

data class SwitchCapability(
    val switch: ValueWithTimestamp?
)

data class HealthCheckCapability(
    val checkInterval: IntervalValue? = null,
    val healthStatus: ValueWithTimestamp? = null,
    val `DeviceWatch-Enroll`: ValueWithTimestamp? = null,
    val `DeviceWatch-DeviceStatus`: ValueWithTimestamp? = null
)

data class BatteryCapability(
    val battery: ValueWithTimestamp? = null,
    val quantity: ValueWithTimestamp? = null,
    val type: ValueWithTimestamp? = null
)

data class ValueWithTimestamp(
    val value: String?,
    val unit: String? = null,
    val timestamp: String? = null,
    val data: Map<String, Any>? = null
)

data class IntervalValue(
    val value: Int?,
    val unit: String?,
    val timestamp: String?,
    val data: Map<String, Any>? = null
)

fun DeviceStatusResponse.extractDeviceStatus(): Pair<Boolean, Boolean> {
    val mainComponent = this.status.components["main"]
    val isOn = mainComponent?.switch?.switch?.value == "on"
    val isActive = mainComponent?.healthCheck?.`DeviceWatch-DeviceStatus`?.value == "online"
    return isOn to isActive
}
