package com.hogumiwarts.lumos.ui.screens.auth.smartthings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.data.source.remote.SmartThingsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmartThingsViewModel @Inject constructor(
    private val smartThingsApi: SmartThingsApi
) : ViewModel() {

    private val _authResult = MutableStateFlow<Result<Unit>?>(null)
    val authResult: StateFlow<Result<Unit>?> = _authResult

    fun handleSmartThingsCallback(code: String?) {
        viewModelScope.launch {
            try {
                if (code != null) {
                    smartThingsApi.exchangeCodeForToken(code)
                }
                _authResult.value = Result.success(Unit)
            } catch (e: Exception) {
                _authResult.value = Result.failure(e)
            }
        }
    }

    fun clearResult() {
        _authResult.value = null
    }


    suspend fun getAuthUrl(): String {
        return smartThingsApi.getSmartThingsAuthUrl().url
    }
}
