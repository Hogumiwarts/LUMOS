package com.hogumiwarts.lumos.utils.uwb

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class BleScanner @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "BLEScanner"
        private const val QORVO_COMPANY_ID = 0x02E2      // DWM3001CDK
        private const val DEFAULT_SCAN_PERIOD = 10_000L  // 10초
    }
    private val bluetoothAdapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner


    private val discovered = mutableMapOf<String, BleDevice>()
    private val _devices = MutableStateFlow<List<BleDevice>>(emptyList())
    val devices: StateFlow<List<BleDevice>> = _devices

    private var scanning = false
    private var scanCallback: ScanCallback? = null
    private var timeoutJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @SuppressLint("MissingPermission")
    fun startScan(onlyDwm: Boolean = false, scanPeriod: Long = DEFAULT_SCAN_PERIOD) {
        if (scanning || bluetoothLeScanner == null) return
        if (!hasScanPermission()) {
            Log.e(TAG, "BLUETOOTH_SCAN/BLUETOOTH 권한이 없습니다.")
            return
        }
        if (bluetoothAdapter?.isEnabled != true) {
            Log.e(TAG, "블루투스가 꺼져 있습니다.")
            return
        }

        discovered.clear()
        _devices.value = emptyList()

        val filters = if (onlyDwm) {
            listOf(
                ScanFilter.Builder()
                    .setManufacturerData(QORVO_COMPANY_ID, byteArrayOf())
                    .build()
            )
        } else emptyList()

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanCallback = bleCallback
        bluetoothLeScanner.startScan(filters, settings, bleCallback)
        scanning = true
        Log.d(TAG, "BLE 스캔 시작")

        timeoutJob?.cancel()
        timeoutJob = scope.launch {
            delay(scanPeriod)
            stopScan()
            Log.d(TAG, "BLE 스캔 타임아웃 종료")
        }
    }

    fun stopScan() {
        if (!scanning) return
        try {
            scanCallback?.let { bluetoothLeScanner?.stopScan(it) }
        } catch (e: SecurityException) {
            Log.e(TAG, "스캔 중지 권한 오류: ${e.message}")
        }
        scanCallback = null
        timeoutJob?.cancel()
        scanning = false
        Log.d(TAG, "BLE 스캔 중지")
    }

    private val bleCallback = object : ScanCallback() {

        // @SuppressLint 최소 범위: 이름 읽기 한 줄
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device ?: return
            val address = device.address

            val name: String? = if (hasConnectPermission()) device.name else null

            discovered[address] = BleDevice(address, name, result.rssi)
            _devices.value = discovered.values.sortedByDescending { it.rssi }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "스캔 실패: $errorCode")
            stopScan()
        }
    }

    private fun hasScanPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun hasConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

}