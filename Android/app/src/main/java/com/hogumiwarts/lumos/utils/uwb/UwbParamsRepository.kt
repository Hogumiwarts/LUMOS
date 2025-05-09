package com.hogumiwarts.lumos.utils.uwb

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

class UwbParamsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "uwb_params"
        private const val KEY_SUFFIX_UWB_ADDRESS = "_uwb_address"
        private const val KEY_SUFFIX_CHANNEL = "_channel"
        private const val KEY_SUFFIX_STS_KEY = "_sts_key"
    }

    // UWB 파라미터 저장
    fun saveUwbParams(deviceAddress: String, params: UwbParams) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        val addressHex = bytesToHex(params.uwbAddress)
        val stsKeyHex = bytesToHex(params.stsKey)

        editor.putString("${deviceAddress}$KEY_SUFFIX_UWB_ADDRESS", addressHex)
        editor.putInt("${deviceAddress}$KEY_SUFFIX_CHANNEL", params.channel)
        editor.putString("${deviceAddress}$KEY_SUFFIX_STS_KEY", stsKeyHex)
        editor.apply()
    }

    // 저장된 UWB 파라미터 로드
    fun loadUwbParams(deviceAddress: String): UwbParams? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val addressHex = prefs.getString("${deviceAddress}$KEY_SUFFIX_UWB_ADDRESS", null) ?: return null
        val channel = prefs.getInt("${deviceAddress}$KEY_SUFFIX_CHANNEL", 0)
        val stsKeyHex = prefs.getString("${deviceAddress}$KEY_SUFFIX_STS_KEY", null) ?: return null

        if (channel == 0) return null

        return try {
            UwbParams(
                uwbAddress = hexToBytes(addressHex),
                channel = channel,
                stsKey = hexToBytes(stsKeyHex)
            )
        } catch (e: Exception) {
            null
        }
    }

    // 바이트 배열 -> 16진수 문자열
    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString(":") { "%02X".format(it) }
    }

    // 16진수 문자열 -> 바이트 배열
    private fun hexToBytes(hex: String): ByteArray {
        return hex.split(":")
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

    // 저장된 모든 UWB 기기 목록 가져오기
    fun getAllSavedDevices(): List<SavedUwbDevice> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val allKeys = prefs.all.keys

        // 기기 주소 추출 (접미사를 제거하여 고유 기기 주소만 가져옴)
        val deviceAddresses = allKeys
            .filter { it.endsWith(KEY_SUFFIX_UWB_ADDRESS) }
            .map { it.removeSuffix(KEY_SUFFIX_UWB_ADDRESS) }
            .distinct()

        // 각 기기의 UWB 파라미터 로드
        return deviceAddresses.mapNotNull { address ->
            val params = loadUwbParams(address) ?: return@mapNotNull null

            SavedUwbDevice(
                address = address,
                uwbParams = params,
                lastConnected = prefs.getLong("${address}_last_connected", 0)
            )
        }
    }

    // 마지막 연결 시간 업데이트
    fun updateLastConnectedTime(deviceAddress: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong("${deviceAddress}_last_connected", System.currentTimeMillis()).apply()
    }
}