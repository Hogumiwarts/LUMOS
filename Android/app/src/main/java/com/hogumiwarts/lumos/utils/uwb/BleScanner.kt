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
import android.os.ParcelUuid
import android.util.Log
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

class BleScanner @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "BLEScanner"
        private val SERVICE_UUID: UUID = UUID.fromString("2b8d0001-6828-46af-98aa-557761b15400")

//        private const val QORVO_COMPANY_ID = 0x02E2      // DWM3001CDK
        private const val DEFAULT_SCAN_PERIOD = 10_000L  // 10초
    }
    private val bluetoothAdapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

    private val _scanResult = MutableStateFlow<List<BleDevice>>(emptyList())
    val scanResult = _scanResult.asStateFlow()

    private val scanner by lazy {
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager)
            .adapter.bluetoothLeScanner
    }

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


        val filters = if (onlyDwm) {
            listOf(
                ScanFilter.Builder()
                    .setServiceUuid(ParcelUuid(SERVICE_UUID))      // ★
                    // 예) .setDeviceName("CLHMP") 로 더 좁힐 수도 있음
                    .build()
            )
        } else listOf()

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
            val list = _scanResult.value.toMutableList()

            // ① 주소 문자열로 중복 검사
            val index = list.indexOfFirst { it.address == result.device.address }

            // ② 새 항목 생성  ─ address / name / rssi
            val newEntry = BleDevice(
                address = result.device.address,
                name    = result.device.name,   // null 허용
                rssi    = result.rssi
            )

            if (index >= 0) {
                // 기존 항목 업데이트 (RSSI·이름 갱신)
                list[index] = newEntry
            } else {
                // 새 기기 추가
                list.add(newEntry)
            }
            _scanResult.value = list          // StateFlow 갱신

            Log.d(TAG, "발견 → ${newEntry.address}  RSSI=${newEntry.rssi}")
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