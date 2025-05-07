package com.hogumiwarts.lumos.utils.uwb

import android.Manifest
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class BleScanner(private val context: Context) {
    companion object {
        private const val TAG = "BLEScanner"
        // SmartTag2의 디바이스 이름 패턴 (필요에 따라 수정)
        private val SMARTTAG2_NAME_FILTERS = listOf(
            "Galaxy SmartTag2",
            "SmartTag2",
            "Smart Tag2",
            "Smart Tag",
            "SmartTag",
            "Galaxy SmartTag"
        )
    }

    private val bluetoothManager: BluetoothManager? = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

    private var isScanning = false

    // 스캔 결과를 전달할 채널
    private val scanResultsChannel = Channel<BluetoothDevice>(Channel.BUFFERED)
    val scanResults: Flow<BluetoothDevice> = scanResultsChannel.receiveAsFlow()

    // BLE 스캔 콜백
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device

            // 권한 확인
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "BLUETOOTH_CONNECT 권한이 없습니다.")
                return
            }

            val deviceName = try {
                device.name
            } catch (e: SecurityException) {
                Log.e(TAG, "권한 부족으로 디바이스 이름을 가져올 수 없습니다: ${e.message}")
                null
            }

            // SmartTag2 디바이스인지 확인 (이름 패턴 확장)
            if (deviceName != null && SMARTTAG2_NAME_FILTERS.any { deviceName.contains(it, ignoreCase = true) }) {
                Log.d(TAG, "Found SmartTag2: $deviceName, Address: ${device.address}")
                // 채널로 디바이스 전송
                scanResultsChannel.trySend(device)
            } else {
                // 서비스 UUID로도 검사 (이름이 없거나 다른 이름일 경우)
                val scanRecord = result.scanRecord
                if (scanRecord != null) {
                    val serviceUuids = scanRecord.serviceUuids
                    if (serviceUuids != null && serviceUuids.any { it.uuid.toString() == "eedd5e73-6aa8-4673-8219-398a489da87c" }) {
                        Log.d(TAG, "Found SmartTag2 by service UUID: ${device.address}")
                        scanResultsChannel.trySend(device)
                    }
                }
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            for (result in results) {
                onScanResult(0, result)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "BLE scan failed with error code: $errorCode")
            isScanning = false
        }
    }

    // BLE가 지원되는지 확인
    fun isBleSupported(): Boolean {
        // 블루투스 어댑터가 존재하는지 + 기기가 BLE 기능이 있는지
        return bluetoothAdapter != null &&
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }

    // BLE가 활성화되어 있는지 확인
    fun isBleEnabled(): Boolean {
        // Android 12 이상: BLUETOOTH_CONNECT 권한 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return try {
            bluetoothAdapter?.isEnabled == true
        } catch (e: SecurityException) {
            Log.e(TAG, "블루투스 상태 확인 실패: ${e.message}")
            false
        }
    }

    // BLE 권한 확인
    fun hasRequiredPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    // 스캔 시작
    fun startScan() {
        if (!hasRequiredPermissions()) {
            Log.e(TAG, "BLE 스캔에 필요한 권한이 없습니다.")
            return
        }

        if (!isBleEnabled()) {
            Log.e(TAG, "Bluetooth is not enabled")
            return
        }

        if (isScanning) {
            Log.d(TAG, "Already scanning")
            return
        }

        try {
            // 스캔 필터 설정 (SmartTag2 이름으로 필터링할 수도 있음)
            val filters = listOf<ScanFilter>()

            // 스캔 설정
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // 빠른 스캔
                .build()

            // 스캔 시작 - 권한 확인 추가
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "BLUETOOTH_SCAN 권한이 없습니다.")
                return
            }

            bluetoothLeScanner?.startScan(filters, settings, scanCallback)
            isScanning = true
            Log.d(TAG, "BLE scan started")
        } catch (e: SecurityException) {
            Log.e(TAG, "권한 부족으로 BLE 스캔을 시작할 수 없습니다: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting BLE scan: ${e.message}")
        }
    }

    // 스캔 중지
    fun stopScan() {
        if (!isScanning) return

        try {
            // 권한 확인
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "BLUETOOTH_SCAN 권한이 없습니다.")
                return
            }

            bluetoothLeScanner?.stopScan(scanCallback)
            isScanning = false
            Log.d(TAG, "BLE scan stopped")
        } catch (e: SecurityException) {
            Log.e(TAG, "권한 부족으로 BLE 스캔을 중지할 수 없습니다: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping BLE scan: ${e.message}")
        }
    }
}