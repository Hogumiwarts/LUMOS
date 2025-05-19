package com.hogumiwarts.myapplication.presentation.viewmodel
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.myapplication.BuildConfig
import com.hogumiwarts.myapplication.util.GestureConstants
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
import okhttp3.*
import org.json.JSONObject
import javax.inject.Inject



@HiltViewModel
class GestureViewModel @Inject constructor() : ViewModel(), DefaultLifecycleObserver {
    private val _prediction = mutableStateOf("예측 없음")
    val prediction: State<String> = _prediction

    private val _history = mutableStateListOf<String>()
    val history: List<String> = _history

    private var webSocket: WebSocket? = null

    // 특정 제스처에 대한 이벤트를 알리기 위한 이벤트 객체
    private val _gestureEvent = MutableSharedFlow<Int>()
    val gestureEvent = _gestureEvent.asSharedFlow()

    // UI 상태를 관리하는 StateFlow
    private val _uiState = MutableStateFlow(GestureUiState())
    val uiState: StateFlow<GestureUiState> = _uiState.asStateFlow()


    // 제스처 인식 모드 관리
    enum class GestureRecognitionMode {
        INACTIVE,   // 제스처 인식 비활성화 (센서 데이터 처리 안 함)
        ACTIVATING, // 활성화 중 (1번 제스처 감지 후 짧은 시간 동안)
        ACTIVE      // 제스처 인식 활성화 (센서 데이터 처리 및 제스처 2, 3 인식)
    }

    enum class FeedbackPattern {
        MODE_CHANGE,     // 모드 전환 (활성화/비활성화)
        GESTURE_ACTION,  // 제스처 2/3 감지
        ACTIVATION       // 수동 활성화
    }


    private val _recognitionMode = MutableStateFlow(GestureRecognitionMode.INACTIVE)
    val recognitionMode: StateFlow<GestureRecognitionMode> = _recognitionMode.asStateFlow()

    private var activationTimestamp: Long = 0
    private var inactivityTimer: Job? = null
    private var vibrator: Vibrator? = null

    // 콜백을 위한 속성
    var onGesture1Detected: (() -> Unit)? = null
    var onGesture2Detected: (() -> Unit)? = null
    var onGesture3Detected: (() -> Unit)? = null

    // 진동 관련 함수 초기화
    fun initVibrator(context: Context) {
        vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disconnectWebSocket()
        super.onDestroy(owner)
    }

    fun connectWebSocket() {
        if (webSocket != null) return

        val ip = BuildConfig.ipAddress
        val client = OkHttpClient()
        val request = Request.Builder().url("ws://${ip}:8000/ws/gesture").build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "✅ WebSocket 연결 성공")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
//                Log.d("WebSocket", "📩 받은 메시지: $text")

                // 메시지 파싱
                // 1. GestureId 추출
                val gestureId = parseGestureId(text)

                // 2. UI 업데이트 (prediction 값과 history 업데이트)
                updateUI(gestureId, text)


                // 3. 제스처에 따른 동작 처리
                handleGesture(gestureId)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "❌ 연결 실패: ${t.message}")

                // 연결 실패 시 UI 상태 업데이트
                _uiState.update {
                    it.copy(
                        isConnected = false,
                        errorMessage = "서버 연결 실패: ${t.message}"
                    )
                }

                // 재연결 시도
                viewModelScope.launch {
                    delay(5000) // 5초 후 재연결 시도
                    connectWebSocket()
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "WebSocket 연결 종료: $reason")
                _uiState.update { it.copy(isConnected = false) }
            }
        })

        // 연결 시작 시 UI 상태 업데이트
        _uiState.update { it.copy(isConnecting = true) }
    }

    // 메시지를 파싱하여 제스처 ID 추출
    private fun parseGestureId(text: String): Int {
        return try {
            if (text.startsWith("{")) {
                // JSON 형식 응답 처리
                val json = JSONObject(text)
                json.optInt("predicted", -1)
            } else {
                // 숫자 형식 응답 처리
                text.toInt()
            }
        } catch (e: NumberFormatException) {
            -1 // 파싱 실패 시 기본값
        }
    }

    // Gesture UI 업데이트
    private fun updateUI(gestureId: Int, originalText: String) {

        if (originalText.startsWith("{")) {
            // JSON 응답인 경우
            val json = JSONObject(originalText)
            val gestureName = json.optString("gesture_name", "예측 없음")
            _prediction.value = gestureName
            _history.add(0, gestureName)
        } else {
            // 정지 제스처(2,3) 번만 UI 처리
            when (gestureId) {
                1 -> {
                    _prediction.value = "1"
                    // 히스토리에 추가 ❌
                }
                2, 3 -> {
                    _prediction.value = originalText
                    _history.add(0, originalText)  // 히스토리 기록
                }
                else -> {
                    _prediction.value = "-"
//                    _history.add(0, originalText)  // 히스토리 기록 x => 버퍼 아닌 단순 UI용
                }
            }
        }
    }

    /**
     * 제스처 ID에 따른 동작을 처리합니다.
     */
    private fun handleGesture(gestureId: Int) {
        when (gestureId) {
            1 -> handleGesture1()
            2, 3 -> handleActiveGesture(gestureId)
        }
    }

    /**
     * 제스처 1(손목 회전) 처리
     */
    private fun handleGesture1() {
        viewModelScope.launch { _gestureEvent.emit(1) }
        processGesture1Detection()
    }

    /**
     * 활성화 상태의 제스처(2,3) 처리
     */
    private fun handleActiveGesture(gestureId: Int) {
        val currentTime = System.currentTimeMillis()

        // 제스처별 디바운싱 적용
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

        // 활성 모드에서만 처리
        if (!isGestureRecognitionActive()) return

        // 이벤트 발행
        viewModelScope.launch { _gestureEvent.emit(gestureId) }

        // 진동 피드백
        provideHapticFeedback(FeedbackPattern.GESTURE_ACTION)

        // 콜백 호출
        if (gestureId == 2) {
            onGesture2Detected?.invoke()
        } else {
            onGesture3Detected?.invoke()
        }

        // 활동 알림
        notifyActivity()
    }


    fun disconnectWebSocket() {
        webSocket?.close(1000, "종료")
        webSocket = null
        _uiState.update { it.copy(isConnected = false, isConnecting = false) }
    }

    fun sendIMUData(json: String) {
        if (webSocket == null) {
            Log.w("WebSocket", "⚠️ 전송 시도했지만 WebSocket이 닫혀있음")
            return
        }
        webSocket?.send(json)
    }

    /**
     * 제스처 1 감지 처리 - 활성화/비활성화 토글
     */
    // 제스처 1 감지 관련 변수들
    private var lastGesture1DetectionTime = 0L
    private var lastGesture2DetectionTime = 0L  // 새로 추가
    private var lastGesture3DetectionTime = 0L  // 새로 추가
    private val DEBOUNCE_TIME = GestureConstants.GESTURE1_DEBOUNCE_MS  // 모든 제스처에 적용할 동일한 디바운싱 시간

    private var awaitingSecondGesture = false
    private var secondGestureTimer: Job? = null

    // 제스처 1(손목 회전) 감지 처리 - 항상 두 번의 연속 제스처 필요
    fun processGesture1Detection() {
        val currentTime = System.currentTimeMillis()

        // 디바운싱: 연속 감지 방지 (특정 시간내에 중복 감지된 제스처 1 무시)
        if (currentTime - lastGesture1DetectionTime < DEBOUNCE_TIME) {
//            Log.d("GestureMode", "제스처 1 감지: 디바운스 시간 내 무시됨")
            return
        }

        // 감지 시간 업데이트
        lastGesture1DetectionTime = currentTime

        if (awaitingSecondGesture) {
            // 두 번째 제스처 감지됨
            Log.d("GestureMode", "제스처 1 감지: 두 번째 제스처 확인! On/Off 설정 변경")

            // 타이머 취소
            secondGestureTimer?.cancel()

            // 모드 전환 (활성화 또는 비활성화)
            if (_recognitionMode.value == GestureRecognitionMode.INACTIVE) {
                _recognitionMode.value = GestureRecognitionMode.ACTIVE
                _uiState.update {
                    it.copy(
                        isListening = true,
                        showActivationIndicator = true,
                        activationProgress = 1f
                    )
                }
            } else {
                _recognitionMode.value = GestureRecognitionMode.INACTIVE
                _uiState.update {
                    it.copy(
                        isListening = false,
                        showActivationIndicator = false,
                        activationProgress = 0f
                    )
                }
            }

            // 모드 전환 피드백 (짧은 진동 두 번)
            provideHapticFeedback(FeedbackPattern.MODE_CHANGE)

            // 대기 상태 해제
            awaitingSecondGesture = false

        } else {
            // 첫 번째 제스처 감지 - 두 번째 제스처 대기
            awaitingSecondGesture = true

            // 첫 번째 제스처는 햅틱 피드백 없음 (혼란 방지)
            startSecondGestureTimer() // 두 번째 제스처 대기 타이머 시작
            onGesture1Detected?.invoke() // 콜백 호출
        }
    }

    /** 두 번째 제스처 대기 타이머 시작*/
    private fun startSecondGestureTimer() {
        secondGestureTimer?.cancel()
        secondGestureTimer = viewModelScope.launch {
            delay(GestureConstants.DOUBLE_GESTURE_THRESHOLD_MS)
            if (awaitingSecondGesture) {
                Log.d("GestureMode", "두 번째 제스처 대기 시간 초과: 대기 상태 해제")
                awaitingSecondGesture = false

                // 타임아웃 피드백 - 짧은 진동으로 안내
               // provideTimeoutFeedback()
            }
        }
    }


    // 5. 제스처 인식을 비활성화합니다. (루틴 실행만 하지 않을 뿐, 지속적으로 IMU 센서 감지는 함.)
    private fun deactivateGestureRecognition() {
        _recognitionMode.value = GestureRecognitionMode.INACTIVE

        inactivityTimer?.cancel()

        // UI 상태 업데이트
        _uiState.update { it.copy(
            isListening = false,
            showActivationIndicator = false,
            activationProgress = 0f
        ) }

        // 비활성화 피드백
        provideHapticFeedback(FeedbackPattern.MODE_CHANGE)

        Log.d("GestureMode", "제스처 인식 모드 비활성화")
    }



    /**
     * 비활성 타이머를 재설정합니다.
     */
    private fun resetInactivityTimer() {
        inactivityTimer?.cancel()
        // startInactivityTimer()
    }

    /**
     * 활동 감지 시 타이머를 재설정합니다.
     */
    fun notifyActivity() {
        if (_recognitionMode.value == GestureRecognitionMode.ACTIVE ||
            _recognitionMode.value == GestureRecognitionMode.ACTIVATING) {
            resetInactivityTimer()
        }
    }

    /**
     * 센서 데이터 처리를 위한 제스처 인식 활성화 상태 확인
     */
    fun isGestureRecognitionActive(): Boolean {
        return _recognitionMode.value == GestureRecognitionMode.ACTIVE ||
                _recognitionMode.value == GestureRecognitionMode.ACTIVATING
    }

    /**
     * 버튼 클릭으로 제스처 인식 모드 토글
     */
    fun toggleGestureRecognition() {
        if (isGestureRecognitionActive()) {
            deactivateGestureRecognition()
        } else {
            _recognitionMode.value = GestureRecognitionMode.ACTIVE
            activationTimestamp = System.currentTimeMillis()

            // 센서 모드를 NORMAL로 설정
//            setSensorMode(SensorMode.NORMAL)

            // UI 상태 업데이트
            _uiState.update { it.copy(
                isListening = true,
                showActivationIndicator = true,
                activationProgress = 1f
            ) }

            // 햅틱 피드백 제공
            provideHapticFeedback(FeedbackPattern.ACTIVATION)

            // 타이머 시작
            //startInactivityTimer()

            Log.d("GestureMode", "제스처 인식 모드 수동 활성화 (버튼) - 정상 센서 모드로 전환")
        }
    }

    /**
     * 햅틱 피드백을 제공합니다.
     * @param pattern 피드백 패턴 (MODE_CHANGE, GESTURE_ACTION, ACTIVATION)
     */
    private fun provideHapticFeedback(pattern: FeedbackPattern) {
        vibrator?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                when (pattern) {
                    FeedbackPattern.MODE_CHANGE -> {
                        // 짧은 진동 두 번 (모드 전환)
                        val timings = longArrayOf(0, 30, 50, 30)
                        val amplitudes = intArrayOf(0, 255, 0, 255)
                        it.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    }
                    FeedbackPattern.GESTURE_ACTION -> {
                        // 짧은 진동 한 번 (제스처 동작)
                        it.vibrate(VibrationEffect.createOneShot(50, GestureConstants.VIBRATION_AMPLITUDE_STRONG))
                    }
                    FeedbackPattern.ACTIVATION -> {
                        // 중간 진동 한 번 (수동 활성화)
                        it.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                when (pattern) {
                    FeedbackPattern.MODE_CHANGE -> it.vibrate(longArrayOf(0, 30, 50, 30), -1)
                    FeedbackPattern.GESTURE_ACTION -> it.vibrate(50)
                    FeedbackPattern.ACTIVATION -> it.vibrate(100)
                }
            }
        }
    }
}