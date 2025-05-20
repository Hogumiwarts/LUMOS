package com.hogumiwarts.lumos

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import com.hogumiwarts.lumos.ui.screens.auth.login.LoginState
import com.hogumiwarts.lumos.ui.screens.gesture.GestureState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class GestureTestViewModel @Inject constructor(

) : ViewModel() {
    private val _message = mutableStateOf("제스처 인식중")
    val message: State<String> = _message


    fun updateMessage(newMsg: String) {
        _message.value = newMsg
    }

}