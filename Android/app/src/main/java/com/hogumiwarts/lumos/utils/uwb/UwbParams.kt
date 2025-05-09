package com.hogumiwarts.lumos.utils.uwb

data class UwbParams(
    val uwbAddress: ByteArray,
    val channel: Int,
    val stsKey: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UwbParams

        if (!uwbAddress.contentEquals(other.uwbAddress)) return false
        if (channel != other.channel) return false
        if (!stsKey.contentEquals(other.stsKey)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uwbAddress.contentHashCode()
        result = 31 * result + channel
        result = 31 * result + stsKey.contentHashCode()
        return result
    }
}

// 저장된 UWB 기기 정보를 담을 데이터 클래스
data class SavedUwbDevice(
    val address: String,
    val uwbParams: UwbParams,
    val lastConnected: Long
)