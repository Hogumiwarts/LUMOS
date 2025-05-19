package com.hogumiwarts.lumos.presentation.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.lumos.BuildConfig
import com.hogumiwarts.lumos.domain.model.routine.PostRoutineResult
import com.hogumiwarts.lumos.domain.repository.RoutineRepository
import com.hogumiwarts.lumos.domain.usecase.RoutineUseCase
import com.hogumiwarts.lumos.domain.usecase.SwitchUseCase
import com.hogumiwarts.lumos.presentation.ui.screens.gesture.GestureMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class WebSocketViewModel @Inject constructor(
    private val routineUseCase: RoutineUseCase
) : ViewModel() {

    private val _prediction = mutableStateOf("예측 없음")
    val prediction: State<String> = _prediction

    private var webSocket: WebSocket? = null
    private var isConnecting = false
    private var currentMode = GestureMode.TEST

    fun connectWebSocket(mode: GestureMode = GestureMode.CONTINUOUS) {

        if (webSocket != null || isConnecting) {
            Log.d("WebSocket", "이미 연결 중이거나 시도 중입니다.")
            return
        }

        currentMode = mode
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
                Log.d("Routine", "onMessage: $currentMode")
                isConnecting = false
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "📩 받은 메시지: $text")
                Log.d("Routine", "현재 모드: $currentMode")

                // 🔥 서버가 단순 문자열로 보내는 경우와 JSON으로 보내는 경우 모두 처리
                val label = if (text.startsWith("{")) {
                    // JSON 형태인 경우
                    try {
                        val json = JSONObject(text)
                        json.optString("label", "예측 없음")
                    } catch (e: Exception) {
                        Log.e("WebSocket", "JSON 파싱 오류", e)
                        text
                    }
                } else {
                    // 단순 문자열인 경우 (현재 서버 응답)
                    text
                }

                Log.d("Routine", "파싱된 라벨: '$label', 모드: $currentMode")
                _prediction.value = label

                when (currentMode) {
                    GestureMode.TEST -> {
                        Log.d("Routine", "🧪 TEST 모드 - prediction만 업데이트")
                        _prediction.value = label
                    }

                    GestureMode.CONTINUOUS -> {
                        Log.d("Routine", "🔄 CONTINUOUS 모드 - 제스처 확인 중...")
                        _prediction.value = label

                        // 🔍 조건 체크
                        val isGestureDetected = label != "0" && label != "5" && label != "6" && label != "예측 없음"
                        Log.d("Routine", "🎯 제스처 감지 조건: label='$label', 감지됨=$isGestureDetected")

                        if (isGestureDetected) {
                            Log.d("Routine", "🚀🚀🚀 루틴 실행! executeGestureRoutine('$label') 호출")
                            executeGestureRoutine(label)
                        } else {
                            Log.d("Routine", "⏸️ 루틴 실행 조건 불만족 (대기 상태: $label)")
                        }
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "❌ 연결 실패: ${t.message}")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "🔒 WebSocket 연결 종료: $code, $reason")
                isConnecting = false
            }
        })
    }

    fun disconnectWebSocket() {
        webSocket?.close(1000, "종료")
        webSocket = null
        isConnecting = false
        Log.d("WebSocket", "🔌 웹소켓 연결 끊김")
    }

    fun sendIMUData(json: String) {
        if (webSocket == null) {
            Log.w("WebSocket", "⚠️ 전송 시도했지만 WebSocket이 닫혀있음")
            return
        }
        Log.d("WebSocket", "📤 메시지 전송: $json")
        webSocket?.send(json)
    }

    // 제스처 루틴 실행 (연속 감지 모드용)
    private fun executeGestureRoutine(gestureId: String) {
        Log.d("Routine", "🎯 제스처 $gestureId 인식! 루틴 실행")

        viewModelScope.launch {
            try {
                when (val result = routineUseCase.postRoutineExecute(gestureId.toLong())) {
                    is PostRoutineResult.Success -> {
                        if (result.data.success) {
                            Log.d("Routine", "✅ 루틴 실행 성공")
                            _prediction.value = "루틴 실행 완료"
                            kotlinx.coroutines.delay(5000)
                            _prediction.value = "예측 없음"

                        } else {
                            Log.e("Routine", "❌ 루틴 실행 실패")
                            _prediction.value = "루틴 실행 실패"
                            kotlinx.coroutines.delay(5000)
                            _prediction.value = "예측 없음"
                        }
                    }

                    is PostRoutineResult.Error -> {
                        Log.e("WebSocket", "❌ 루틴 실행 오류: ${result.error}")
                        _prediction.value = "루틴 실행 오류"
                        kotlinx.coroutines.delay(5000)
                        _prediction.value = "예측 없음"
                    }
                }
            } catch (e: Exception) {
                Log.e("WebSocket", "루틴 실행 중 예외 발생", e)
                _prediction.value = "루틴 실행 오류"
                kotlinx.coroutines.delay(2000)
                _prediction.value = "예측 없음"
            }
        }
    }
}