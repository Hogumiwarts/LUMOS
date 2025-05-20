package com.hogumiwarts.lumos.presentation.ui.viewmodel

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.lumos.BuildConfig
import com.hogumiwarts.lumos.domain.model.routine.PostRoutineResult
import com.hogumiwarts.lumos.domain.usecase.RoutineUseCase
import com.hogumiwarts.lumos.presentation.ui.screens.gesture.GestureMode
import com.hogumiwarts.lumos.presentation.ui.screens.gesture.GestureUiState
import com.hogumiwarts.lumos.util.GestureConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    // 1번 제스처 인식
    private val _test1 = MutableStateFlow(false)
    val test1: StateFlow<Boolean> = _test1

    // 테스트 실행 여부
    private val _isTest = MutableStateFlow(false)
    val isTest: StateFlow<Boolean> = _isTest

    // 테스트 실행 여부
    private val _gesture2 = MutableStateFlow(false)
    val gesture2: StateFlow<Boolean> = _gesture2

    fun resetTest1() {
        _test1.value = false
    }

    private var webSocket: WebSocket? = null
    private var currentMode = GestureMode.TEST

    // UI 상태를 관리하는 StateFlow
    private val _uiState = MutableStateFlow(GestureUiState())
    val uiState: StateFlow<GestureUiState> = _uiState.asStateFlow()

    // 제스처 인식 모드 관리
    enum class GestureRecognitionMode {
        INACTIVE,   // 제스처 인식 비활성화 (2,3번 제스처 처리 안 함)
        ACTIVATING, // 활성화 중 (1번 제스처 감지 후 짧은 시간 동안)
        ACTIVE      // 제스처 인식 활성화 (2,3번 제스처 처리 및 execute 실행)
    }

    enum class FeedbackPattern {
        MODE_CHANGE,     // 모드 전환 (활성화/비활성화)
        GESTURE_ACTION,  // 제스처 2, 3 감지
        ACTIVATION,      // 수동 활성화
        ROUTINE_SUCCESS  // 루틴 실행 성공
    }

    // 제스처 이벤트
    private val _gestureEvent = MutableSharedFlow<Int>()
    val gestureEvent = _gestureEvent.asSharedFlow()

    // 제스처 인식 모드 (CONTINUOUS에서만 사용)
    private val _recognitionMode = MutableStateFlow(GestureRecognitionMode.INACTIVE)
    val recognitionMode: StateFlow<GestureRecognitionMode> = _recognitionMode.asStateFlow()

    private var activationTimestamp: Long = 0  // 제스처 인식이 활성화된 시간 기록
    private var inactivityTimer: Job? = null   // 일정 시간 동안 제스처 활동이 없으면 자동으로 INACTIVE로 전환
    private var vibrator: Vibrator? = null
    var isConnecting: Boolean = false

    // 콜백을 위한 속성
    var onGesture1Detected: (() -> Unit)? = null
    var onGesture2Detected: (() -> Unit)? = null
    var onGesture3Detected: (() -> Unit)? = null

    // 제스처 1 감지 관련 변수들
    private var lastGesture1DetectionTime = 0L
    private var lastGesture2DetectionTime = 0L
    private var lastGesture3DetectionTime = 0L
    private val DEBOUNCE_TIME = GestureConstants.GESTURE1_DEBOUNCE_MS

    private var awaitingSecondGesture = false
    private var secondGestureTimer: Job? = null


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
        val request = Request.Builder().url("ws://lipit.store/ws/gesture").build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "✅ WebSocket 연결 성공 - 모드: $currentMode")
                isConnecting = false
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "📩 받은 메시지: $text (모드: $currentMode)")
                if (text == "1" || text == "2" || text == "3" || text == "4") {
                    _test1.value = true
                }

                // 메시지 파싱
                val label = if (text.startsWith("{")) {
                    try {
                        val json = JSONObject(text)
                        json.optString("label", "예측 없음")
                    } catch (e: Exception) {
                        Log.e("WebSocket", "JSON 파싱 오류", e)
                        text
                    }
                } else {
                    text
                }

                // 제스처 ID로 변환
                val gestureId = try {
                    label.toInt()
                } catch (e: NumberFormatException) {
                    -1
                }

                Log.d("Routine", "파싱된 라벨: '$label', 제스처 ID: $gestureId, 모드: $currentMode")

                // 모든 모드에서 prediction 업데이트
                _prediction.value = label

                // 모드별 처리
                when (currentMode) {
                    GestureMode.TEST -> {
                        Log.d("Routine", "🧪 TEST 모드 - prediction만 업데이트")
                        // TEST 모드에서는 제스처 처리나 execute 실행 안 함
                    }

                    GestureMode.CONTINUOUS -> {
                        Log.d("Routine", "🔄 CONTINUOUS 모드 - 제스처 처리 중...")
                        handleGestureInContinuousMode(gestureId, label)
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "❌ 연결 실패: ${t.message}")
                isConnecting = false

                // 재연결 시도
                viewModelScope.launch {
                    delay(5000)
                    connectWebSocket(currentMode)
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "🔒 WebSocket 연결 종료: $code, $reason")
                isConnecting = false
            }
        })
    }

    /**
     * CONTINUOUS 모드에서 제스처 처리
     */
    private fun handleGestureInContinuousMode(gestureId: Int, label: String) {
        when (gestureId) {
            1 -> {
                // 1번 제스처: 활성화/비활성화 토글 (두 번 연속 필요)
                processGesture1Detection()
            }

            2 -> {
                processGesture2Detection()
            }

            3 -> {
                // 2,3번 제스처: ACTIVE 상태에서만 처리
                if (_recognitionMode.value == GestureRecognitionMode.ACTIVE) {
                    handleActiveGesture(gestureId, label)
                } else {
                    Log.d("Routine", "⏸️ ${_recognitionMode.value} 상태 - 제스처 $gestureId 무시")
                }
            }

            else -> {
                // 0, 5, 6번 또는 기타 제스처는 무시
                Log.d("Routine", "⏸️ 처리 대상이 아닌 제스처: $gestureId")
            }
        }
    }

    /**
     * 제스처 1 감지 처리 - 두 번 연속으로 활성화/비활성화 토글
     */
    private var gesture1Count = 0
    private var gesture1Timer: Job? = null

    private var gesture2Count = 0
    private var gesture2Timer: Job? = null

    // 현재 실행 중인 루틴을 추적하는 StateFlow 추가
    private val _currentExecutingRoutine = MutableStateFlow<Long?>(null)
    val currentExecutingRoutine: StateFlow<Long?> = _currentExecutingRoutine.asStateFlow()

    private fun processGesture2Detection() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastGesture2DetectionTime < DEBOUNCE_TIME) {
            Log.d("GestureViewModel", "---- (제스처 2 디바운싱: 무시됨)")
            return
        }
        lastGesture1DetectionTime = currentTime

        gesture2Count++
        gesture2Timer?.cancel()
        gesture2Timer = viewModelScope.launch {
            delay(1500) // 1.5초 안에 연속 감지 안 되면 초기화
            Log.d("결과", "🎯 제스처 2번 2회 감지 → 초기화")
            gesture2Count = 0
        }
        if (gesture2Count >= 2) {
            Log.d("결과", "🎯 제스처 2번 2회 감지 → 테스트 실행")
            _gesture2.value = true
            _currentExecutingRoutine.value = 2L
            executeGestureRoutine("2")
            gesture2Count = 0
//            handleActiveGesture(3, "3") // 제스처 3으로 처리
            return
        }

    }

    private fun processGesture1Detection() {

        val currentTime = System.currentTimeMillis()

        if (currentTime - lastGesture1DetectionTime < DEBOUNCE_TIME) {
            Log.d("GestureViewModel", "---- (제스처 2 디바운싱: 무시됨)")
            return
        }
        lastGesture1DetectionTime = currentTime

        gesture1Count++
        gesture1Timer?.cancel()
        gesture1Timer = viewModelScope.launch {
            delay(1500) // 1.5초 안에 연속 감지 안 되면 초기화
            Log.d("결과", "🎯 제스처 1번 2회 감지 → 초기화")
            gesture1Count = 0
        }
        if (gesture1Count >= 2) {
            Log.d("결과", "🎯 제스처 1번 2회 감지 → 테스트 실행")
            _currentExecutingRoutine.value = 1L
            executeGestureRoutine("1")

            _isTest.value = !_isTest.value
            gesture1Count = 0

            return
        }

    }

    /**
     * 활성화 상태의 제스처(2,3) 처리
     */
    private fun handleActiveGesture(gestureId: Int, label: String) {
        val currentTime = System.currentTimeMillis()

        // 제스처별 디바운싱
        if (gestureId == 2) {
            if (currentTime - lastGesture2DetectionTime < DEBOUNCE_TIME) {
                Log.d("GestureViewModel", "---- (제스처 2 디바운싱: 무시됨)")
                return
            }
            lastGesture2DetectionTime = currentTime
        } else if (gestureId == 3) {
            if (currentTime - lastGesture3DetectionTime < DEBOUNCE_TIME) {
                Log.d("GestureViewModel", "---- (제스처 3 디바운싱: 무시됨)")
                return
            }
            lastGesture3DetectionTime = currentTime
        }

        Log.d("Routine", "🎯 ACTIVE 모드에서 제스처 $gestureId 감지 - 루틴 실행!")

        // 활성 모드에서만 처리
        if (!isGestureRecognitionActive()) return

        // 이벤트 발행
        viewModelScope.launch { _gestureEvent.emit(gestureId) }

        // 진동 피드백
        provideHapticFeedback(FeedbackPattern.GESTURE_ACTION)

        // 콜백 호출
        when (gestureId) {
            2 -> onGesture2Detected?.invoke()
            3 -> onGesture3Detected?.invoke()
        }

        // 활동 감지 - 타이머 리셋
        notifyActivity()

        // 루틴 실행
        executeGestureRoutine(label)
    }

    /**
     * 센서 데이터 처리를 위한 제스처 인식 활성화 상태 확인
     */
    private fun isGestureRecognitionActive(): Boolean {
        return _recognitionMode.value == GestureRecognitionMode.ACTIVE ||
                _recognitionMode.value == GestureRecognitionMode.ACTIVATING
    }

    /**
     * 제스처 루틴 실행
     */

    private fun executeGestureRoutine(gestureId: String) {
        Log.d("Routine", "🚀 제스처 $gestureId 루틴 실행 시작")

        viewModelScope.launch {
            try {
                when (val result = routineUseCase.postRoutineExecute(gestureId.toLong())) {
                    is PostRoutineResult.Success -> {
                        if (result.data.success) {
                            provideHapticFeedback(FeedbackPattern.ROUTINE_SUCCESS)
                            Log.d("Routine", "✅ 루틴 실행 성공")
                            _prediction.value = "루틴 실행 완료"
                            delay(3000)
                            _prediction.value = "예측 없음"
                        } else {
                            Log.e("Routine", "❌ 루틴 실행 실패")
                            _prediction.value = "루틴 실행 실패"
                            delay(3000)
                            _prediction.value = "예측 없음"
                        }
                    }

                    is PostRoutineResult.Error -> {
                        Log.e("WebSocket", "❌ 루틴 실행 오류: ${result.error}")
                        _prediction.value = "루틴 실행 오류"
                        delay(3000)
                        _prediction.value = "예측 없음"
                    }
                }
            } catch (e: Exception) {
                Log.e("WebSocket", "루틴 실행 중 예외 발생", e)
                _prediction.value = "루틴 실행 오류"
                delay(2000)
                _prediction.value = "예측 없음"
            } finally {
                // ✅ 루틴 실행 완료 후 상태 리셋
                _currentExecutingRoutine.value = null
            }
        }
    }

    /**
     * 진동 피드백 제공
     */
    private fun provideHapticFeedback(pattern: FeedbackPattern) {
        vibrator?.let {
            when (pattern) {
                FeedbackPattern.MODE_CHANGE -> {
                    // 짧은 진동 두 번 (모드 전환)
                    val timings = longArrayOf(0, 30, 50, 30)
                    val amplitudes = intArrayOf(0, 255, 0, 255)
                    it.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                }

                FeedbackPattern.GESTURE_ACTION -> {
                    // 짧은 진동 한 번 (제스처 동작)
                    it.vibrate(VibrationEffect.createOneShot(50, 255))
                }

                FeedbackPattern.ACTIVATION -> {
                    // 중간 진동 한 번 (수동 활성화)
                    it.vibrate(
                        VibrationEffect.createOneShot(
                            100,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                }

                FeedbackPattern.ROUTINE_SUCCESS -> {
                    // 루틴 실행 성공
                    val timings = longArrayOf(0, 150, 40, 100, 40, 100, 40, 150)
                    val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255, 0, 255)
                    it.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                }
            }
        }
    }


    // 진동 관련 함수 초기화
    fun initVibrator(context: Context) {
        vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }


    /**
     * 제스처 활동 감지 시 호출
     */
    private fun notifyActivity() {
        if (_recognitionMode.value == GestureRecognitionMode.ACTIVE ||
            _recognitionMode.value == GestureRecognitionMode.ACTIVATING
        ) {
            inactivityTimer?.cancel()
        }
    }


    fun disconnectWebSocket() {
        webSocket?.close(1000, "종료")
        webSocket = null
        isConnecting = false

        // 모든 타이머 취소
        inactivityTimer?.cancel()
        secondGestureTimer?.cancel()

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

}