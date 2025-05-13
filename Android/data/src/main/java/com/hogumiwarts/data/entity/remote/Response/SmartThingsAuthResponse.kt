package com.hogumiwarts.data.entity.remote.Response

data class SmartThingsAuthResponse(
    val url: String
)

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