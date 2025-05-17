package com.hogumiwarts.lumos.ui.common

import android.os.Parcel
import android.os.Parcelable
import com.hogumiwarts.domain.model.routine.CommandData
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType

data class MyDevice(
    val deviceId: Int,
    val deviceName: String,
    val isOn: Boolean,
    val isActive: Boolean,
    val deviceType: DeviceListType,
    val commands: List<CommandData> = emptyList()

) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(deviceId)
        parcel.writeString(deviceName)
        parcel.writeByte(if (isOn) 1 else 0)
        parcel.writeByte(if (isActive) 1 else 0)
        parcel.writeString(deviceType.name)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<MyDevice> {
        override fun createFromParcel(parcel: Parcel): MyDevice {
            return MyDevice(
                deviceId = parcel.readInt(),
                deviceName = parcel.readString() ?: "",
                isOn = parcel.readByte() != 0.toByte(),
                isActive = parcel.readByte() != 0.toByte(),
                deviceType = DeviceListType.valueOf(parcel.readString() ?: DeviceListType.ETC.name)
            )
        }

        override fun newArray(size: Int): Array<MyDevice?> = arrayOfNulls(size)

        val sample = listOf(
            MyDevice(1, "거실 공기청정기", true, true, DeviceListType.AIRPURIFIER),
            MyDevice(2, "침대 조명 스위치", false, true, DeviceListType.SWITCH),
            MyDevice(3, "내 방 조명", false, true, DeviceListType.LIGHT),
            MyDevice(4, "음악 플레이어", false, false, DeviceListType.AUDIO)
        )

    }
}

