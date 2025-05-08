package com.hogumiwarts.lumos.ui.screens.control

import androidx.lifecycle.ViewModel
import com.hogumiwarts.lumos.utils.uwb.BleDevice
import com.hogumiwarts.lumos.utils.uwb.BleScanner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BleScannerViewModel @Inject constructor(
    private val bleScanner: BleScanner
) : ViewModel() {

    val devices: StateFlow<List<BleDevice>> = bleScanner.devices

    fun startScan(onlyDwm: Boolean = false) {
        bleScanner.startScan(onlyDwm)
    }

    fun stopScan() {
        bleScanner.stopScan()
    }

    override fun onCleared() {
        super.onCleared()
        stopScan()
    }
}