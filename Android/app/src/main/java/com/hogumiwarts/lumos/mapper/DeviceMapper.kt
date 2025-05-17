package com.hogumiwarts.lumos.mapper

import com.hogumiwarts.domain.model.DeviceResult
import com.hogumiwarts.domain.model.routine.CommandData
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType
import com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.LightRoutineControlState

fun DeviceResult.toMyDevice(): MyDevice {
    return MyDevice(
        deviceId = deviceId.toString(),
        deviceName = deviceName,
        deviceType = deviceType.toDeviceListType(),
        isOn = activated,
        isActive = activated
    )
}

fun String.toDeviceListType(): DeviceListType {
    return when (this.uppercase()) {
        "SWITCH" -> DeviceListType.SWITCH
        "LIGHT" -> DeviceListType.LIGHT
        "AIRPURIFIER" -> DeviceListType.AIRPURIFIER
        "SPEAKER" -> DeviceListType.AUDIO
        else -> DeviceListType.ETC // enum에 없는 경우 대비
    }
}


fun MyDevice.toCommandDevice(
    isOn: Boolean = this.isOn,
    brightness: Int = 50,
    hue: Float = 0f,
    saturation: Float = 100f
): CommandDevice {
    val commands = mutableListOf<CommandData>()

    commands.add(
        CommandData(
            component = "main",
            capability = "switch",
            command = if (isOn) "on" else "off",
            arguments = emptyList()
        )
    )

    commands.add(
        CommandData(
            component = "main",
            capability = "switchLevel",
            command = "setLevel",
            arguments = listOf(brightness)
        )
    )

    commands.add(
        CommandData(
            component = "main",
            capability = "colorControl",
            command = "setColor",
            arguments = listOf(
                mapOf(
                    "hue" to hue,
                    "saturation" to saturation
                )
            )
        )
    )

    return CommandDevice(
        deviceId = deviceId,
        deviceName = deviceName,
        deviceType = deviceType.toString(),
        deviceImageUrl = "",
        commands = commands
    )
}
