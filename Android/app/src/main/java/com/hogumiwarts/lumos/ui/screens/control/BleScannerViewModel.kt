package com.hogumiwarts.lumos.ui.screens.control

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.lumos.utils.uwb.BleDevice
import com.hogumiwarts.lumos.utils.uwb.BleScanner
import com.hogumiwarts.lumos.utils.uwb.GattConnector
import com.hogumiwarts.lumos.utils.uwb.SavedUwbDevice
import com.hogumiwarts.lumos.utils.uwb.UwbParams
import com.hogumiwarts.lumos.utils.uwb.UwbParamsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BleScannerViewModel @Inject constructor(
    private val bleScanner: BleScanner,
    private val gattConnector: GattConnector,
    private val uwbParamsRepository: UwbParamsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    /** 1. BLE 스캔된 기기 목록 */
    val devices: StateFlow<List<BleDevice>> = bleScanner.scanResult

    /** 2. GATT 연결 상태 */
    val connectionState: StateFlow<GattConnector.ConnectionState> = gattConnector.connectionState

    /** 3. 선택된 기기 */
    private val _selectedDevice = MutableStateFlow<BleDevice?>(null)
    val selectedDevice: StateFlow<BleDevice?> = _selectedDevice

    /** 4. 읽어온 UWB 파라미터 */
    private val _uwbParams = MutableStateFlow<UwbParams?>(null)
    val uwbParams: StateFlow<UwbParams?> = _uwbParams

    // 저장된 UWB 기기 목록
    private val _savedDevices = MutableStateFlow<List<SavedUwbDevice>>(emptyList())
    val savedDevices: StateFlow<List<SavedUwbDevice>> = _savedDevices


    init {
        // GATT에서 읽은 UWB 파라미터 자동 업데이트 :contentReference[oaicite:2]{index=2}:contentReference[oaicite:3]{index=3}
        viewModelScope.launch {
            gattConnector.uwbParams.filterNotNull().collect { params ->
                _uwbParams.value = params
            }
        }
// 저장된 기기 목록 초기 로드 :contentReference[oaicite:4]{index=4}:contentReference[oaicite:5]{index=5}
        loadSavedDevices()
    }

    // 저장된 기기 목록 로드
    fun loadSavedDevices() {
        viewModelScope.launch {
            _savedDevices.value = uwbParamsRepository.getAllSavedDevices()
        }
    }

    fun startScan(onlyDwm: Boolean = false) {
        bleScanner.startScan(onlyDwm)
    }

    fun stopScan() {
        bleScanner.stopScan()
    }

    fun connectToDevice(device: BleDevice) {
        _selectedDevice.value = device

        // BluetoothDevice 객체 얻기
        val bluetoothAdapter =
            (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        val btDevice = bluetoothAdapter.getRemoteDevice(device.address)


        viewModelScope.launch {
            // GATT 연결 및 파라미터 읽기 시작 :contentReference[oaicite:6]{index=6}:contentReference[oaicite:7]{index=7}
            gattConnector.connect(btDevice, viewModelScope)

            // READY 상태 감시
            gattConnector.connectionState
                .filter { it == GattConnector.ConnectionState.READY }
                .first()  // 최초 READY 이벤트
                .let {
                    // 파라미터 저장소에 마지막 연결 시간 기록
                    uwbParamsRepository.updateLastConnectedTime(device.address)
                    loadSavedDevices()
                }
        }

//        try {
//            val bluetoothDevice: BluetoothDevice = bluetoothAdapter.getRemoteDevice(device.address)
//
//            // GattConnector를 사용하여 연결 시작
//            viewModelScope.launch {
//                gattConnector.connect(bluetoothDevice, viewModelScope)
//
//                // 연결 성공 시 마지막 연결 시간 업데이트
//                gattConnector.connectionState.collect { state ->
//                    if (state == GattConnector.ConnectionState.READY) {
//                        uwbParamsRepository.updateLastConnectedTime(device.address)
//                        loadSavedDevices() // 목록 갱신
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            // 예외 처리
//        }
    }

    // 저장된 기기로 연결
    fun connectToSavedDevice(saved: SavedUwbDevice) {
        _selectedDevice.value = BleDevice(saved.address, null, 0)

        val bluetoothAdapter =
            (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
                ?: return
        val btDevice = bluetoothAdapter.getRemoteDevice(saved.address)

        viewModelScope.launch {
            gattConnector.connect(btDevice, viewModelScope)
            gattConnector.connectionState
                .filter { it == GattConnector.ConnectionState.READY }
                .first()
                .let {
                    uwbParamsRepository.updateLastConnectedTime(saved.address)
                    loadSavedDevices()
                }
        }
//        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
//
//        if (bluetoothAdapter == null) return
//
//        try {
//            val bluetoothDevice: BluetoothDevice = bluetoothAdapter.getRemoteDevice(savedDevice.address)
//
//            // 저장된 기기에 연결 시도
//            viewModelScope.launch {
//                gattConnector.connect(bluetoothDevice, viewModelScope)
//
//                // 연결 성공 시 마지막 연결 시간 업데이트
//                gattConnector.connectionState.collect { state ->
//                    if (state == GattConnector.ConnectionState.READY) {
//                        uwbParamsRepository.updateLastConnectedTime(savedDevice.address)
//                        loadSavedDevices() // 목록 갱신
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            // 예외 처리
//        }
    }

    fun disconnectDevice() {
        gattConnector.disconnect()
        _selectedDevice.value = null
    }

    override fun onCleared() {
        super.onCleared()
        stopScan()
        gattConnector.cleanup()
    }
}