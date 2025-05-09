package com.hogumiwarts.lumos

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State

class GestureTestViewModel : ViewModel() {
    private val _message = mutableStateOf("기본 메시지")
    val message: State<String> = _message

    fun updateMessage(newMsg: String) {
        _message.value = newMsg
    }
}