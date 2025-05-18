package com.hogumiwarts.lumos.mapper

import com.hogumiwarts.domain.model.DeviceResult
import com.hogumiwarts.domain.model.routine.CommandData
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType

fun DeviceResult.toMyDevice(): MyDevice {
    return MyDevice(
        deviceId = deviceId,
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
        "AUDIO" -> DeviceListType.AUDIO
        else -> DeviceListType.ETC // enum에 없는 경우 대비
    }
}


fun MyDevice.toCommandDevice(
    isOn: Boolean = this.isOn,
    brightness: Int = 50,
    hue: Float? = 0f,
    saturation: Float? = 100f
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
            arguments = listOfNotNull(
                mapOf(
                    "hue" to hue,
                    "saturation" to saturation
                ).takeIf { it.values.none { value -> value == null } }
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

fun MyDevice.toCommandDeviceForAirPurifier(
    isOn: Boolean,
    fanMode: String
): CommandDevice {
    val commands = listOf(
        CommandData(
            component = "main",
            capability = "switch",
            command = if (isOn) "on" else "off",
            arguments = emptyList()
        ),
        CommandData(
            component = "main",
            capability = "airConditionerFanMode",
            command = "setFanMode",
            arguments = listOf(fanMode)
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


fun MyDevice.toCommandDeviceForSpeaker(
    isOn: Boolean,
    volume: Int,
    isPlaying: Boolean
): CommandDevice {
    val commandList = mutableListOf<CommandData>()

    // 볼륨 명령
    commandList.add(
        CommandData(
            component = "main",
            capability = "audioVolume",
            command = "setVolume",
            arguments = listOf(volume)
        )
    )

    // 재생/정지 명령
    commandList.add(
        CommandData(
            component = "main",
            capability = "mediaPlayback",
            command = if (isPlaying) "play" else "stop",
            arguments = emptyList()
        )
    )

    return CommandDevice(
        deviceId = this.deviceId,
        deviceName = this.deviceName,
        deviceType = this.deviceType.toString(),
        deviceImageUrl = "", // 필요 시 이미지 URL 연결
        commands = commandList
    )
}


fun MyDevice.toCommandDeviceForSwitch(
    isOn: Boolean
): CommandDevice {
    val commands = listOf(
        CommandData(
            component = "main",
            capability = "switch",
            command = if (isOn) "on" else "off",
            arguments = emptyList()
        )
    )

    return CommandDevice(
        deviceId = this.deviceId,
        deviceName = this.deviceName,
        deviceType = this.deviceType.toString(),
        deviceImageUrl = "",
        commands = commands
    )
}

