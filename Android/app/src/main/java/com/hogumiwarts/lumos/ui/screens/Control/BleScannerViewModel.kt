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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BleScannerViewModel @Inject constructor(
    private val bleScanner: BleScanner,
    private val gattConnector: GattConnector,
    private val uwbParamsRepository: UwbParamsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val devices: StateFlow<List<BleDevice>> = bleScanner.devices
    val connectionState: StateFlow<GattConnector.ConnectionState> = gattConnector.connectionState

    private val _selectedDevice = MutableStateFlow<BleDevice?>(null)
    val selectedDevice: StateFlow<BleDevice?> = _selectedDevice

    private val _uwbParams = MutableStateFlow<UwbParams?>(null)
    val uwbParams: StateFlow<UwbParams?> = _uwbParams

    // 저장된 UWB 기기 목록
    private val _savedDevices = MutableStateFlow<List<SavedUwbDevice>>(emptyList())
    val savedDevices: StateFlow<List<SavedUwbDevice>> = _savedDevices


    init {
        // UWB 파라미터 상태 관찰
        viewModelScope.launch {
            gattConnector.uwbParams.collectLatest { params ->
                _uwbParams.value = params
            }
        }

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

        // 해당 주소의 BluetoothDevice 얻기
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter ?: return

        try {
            val bluetoothDevice: BluetoothDevice = bluetoothAdapter.getRemoteDevice(device.address)

            // GattConnector를 사용하여 연결 시작
            viewModelScope.launch {
                gattConnector.connect(bluetoothDevice, viewModelScope)

                // 연결 성공 시 마지막 연결 시간 업데이트
                gattConnector.connectionState.collect { state ->
                    if (state == GattConnector.ConnectionState.READY) {
                        uwbParamsRepository.updateLastConnectedTime(device.address)
                        loadSavedDevices() // 목록 갱신
                    }
                }
            }
        } catch (e: Exception) {
            // 예외 처리
        }
    }

    // 저장된 기기로 연결
    fun connectToSavedDevice(savedDevice: SavedUwbDevice) {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

        if (bluetoothAdapter == null) return

        try {
            val bluetoothDevice: BluetoothDevice = bluetoothAdapter.getRemoteDevice(savedDevice.address)

            // 저장된 기기에 연결 시도
            viewModelScope.launch {
                gattConnector.connect(bluetoothDevice, viewModelScope)

                // 연결 성공 시 마지막 연결 시간 업데이트
                gattConnector.connectionState.collect { state ->
                    if (state == GattConnector.ConnectionState.READY) {
                        uwbParamsRepository.updateLastConnectedTime(savedDevice.address)
                        loadSavedDevices() // 목록 갱신
                    }
                }
            }
        } catch (e: Exception) {
            // 예외 처리
        }
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