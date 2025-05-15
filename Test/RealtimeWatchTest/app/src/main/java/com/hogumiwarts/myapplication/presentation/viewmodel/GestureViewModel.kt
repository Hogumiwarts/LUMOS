package com.hogumiwarts.myapplication.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.*
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class GestureViewModel @Inject constructor() : ViewModel() {
    private val _prediction = mutableStateOf("예측 없음")
    val prediction: State<String> = _prediction

    private val _history = mutableStateListOf<String>()
    val history: List<String> = _history

    private var webSocket: WebSocket? = null

    fun connectWebSocket() {
        val client = OkHttpClient()
        val request = Request.Builder().url("ws://[IP-주소]:8000/ws/gesture").build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "✅ WebSocket 연결 성공")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "📩 받은 메시지: $text")
                if (text.startsWith("{")) {
                    val json = JSONObject(text)
                    val label = json.optString("label", "예측 없음")
                    _prediction.value = label
                    _history.add(0, label)
                } else {
                    _prediction.value = text
                    _history.add(0, text)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "❌ 연결 실패: ${t.message}")
            }
        })
    }

    fun disconnectWebSocket() {
        webSocket?.close(1000, "종료")
        webSocket = null
    }

    fun sendIMUData(json: String) {
        if (webSocket == null) {
            Log.w("WebSocket", "⚠️ 전송 시도했지만 WebSocket이 닫혀있음")
            return
        }
        Log.d("WebSocket", "📤 메시지 전송: $json")
        webSocket?.send(json)
    }
}
