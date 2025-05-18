package com.hogumiwarts.lumos.presentation.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hogumiwarts.lumos.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class WebSocketViewModel @Inject constructor() : ViewModel() {

    private val _prediction = mutableStateOf("예측 없음")
    val prediction: State<String> = _prediction

    private var webSocket: WebSocket? = null
    private var isConnecting = false

    fun connectWebSocket() {

        if (webSocket != null || isConnecting) {
            Log.d("WebSocket", "이미 연결 중이거나 시도 중입니다.")
            return
        }
        isConnecting = true
        val ip = BuildConfig.IP_ADDRESS
        if (ip.isBlank()) {
            Log.e("WebSocket", "❌ IP 주소가 설정되지 않았습니다")
            _prediction.value = "서버 IP 설정 오류"
            isConnecting = false
            return
        }

        val client = OkHttpClient()
        val request = Request.Builder().url("ws://${ip}:8000/ws/gesture").build()

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
                } else {
                    _prediction.value = text
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