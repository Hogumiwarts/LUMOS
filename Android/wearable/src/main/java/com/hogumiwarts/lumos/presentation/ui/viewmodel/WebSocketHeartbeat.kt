package com.hogumiwarts.lumos.presentation.ui.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.WebSocket
import org.java_websocket.client.WebSocketClient

/**
 * WebSocketHeartbeat
 *
 * - 일정 주기로 WebSocket 서버에 "PING" 메시지를 보내 연결이 유지되고 있는지 확인합니다.
 * - 서버는 "PONG" 메시지로 응답하며, TTSViewModel에서 해당 응답을 처리합니다.
 * - 서버 연결이 끊겼을 경우 자동으로 중단됩니다.
 * (주후를 보는 내 마음도 하투비투 ❣️)
 */
class WebSocketHeartbeat(
    private val webSocket: WebSocket,
    private val pingMessage: String = "PING",
    private val intervalMillis: Long = 10_000L,
    private val onPingSent: ((String) -> Unit)? = null
) {
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    /** PING 전송 작업 */
    private val pingRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                webSocket.send(pingMessage)
                onPingSent?.invoke(pingMessage)
                Log.d("WebSocket", "💓 하트비트 전송: $pingMessage")

                // 다음 PING 예약
                handler.postDelayed(this, intervalMillis)
            }
        }
    }

    /** 하트비트 시작 */
    fun start() {
        if (!isRunning) {
            isRunning = true
            handler.post(pingRunnable)
            Log.d("WebSocket", "💓 하트비트 시작")
        }
    }

    /** 하트비트 정지 */
    fun stop() {
        isRunning = false
        handler.removeCallbacks(pingRunnable)
        Log.d("WebSocket", "💓 하트비트 정지")
    }
}