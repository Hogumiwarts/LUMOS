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

    enum class SensorMode {
        OFF,       // 센서 완전 비활성화
        LOW_POWER, // 저전력 모드 (1번 제스처만 감지)
        NORMAL     // 정상 모드 (모든 제스처 감지)
    }


    // 기존 코드
//    private val _sensorMode = MutableStateFlow(SensorMode.LOW_POWER)
////    val sensorMode: StateFlow<SensorMode> = _sensorMode.asStateFlow()
//
//    // 추가할 코드
//    private val _sensorModeChanged = MutableSharedFlow<SensorMode>()
////    val sensorModeChanged = _sensorModeChanged.asSharedFlow()
//
//    fun setSensorMode(mode: SensorMode) {
//        if (_sensorMode.value != mode) {
//            _sensorMode.value = mode
//            Log.d("GestureViewModel", "센서 모드 변경: $mode")
//
//            // 센서 모드 변경 이벤트 발행
//            viewModelScope.launch {
//                _sensorModeChanged.emit(mode)
//            }
//        }
//    }

    private val _recognitionMode = MutableStateFlow(GestureRecognitionMode.INACTIVE)
    val recognitionMode: StateFlow<GestureRecognitionMode> = _recognitionMode.asStateFlow()

    private var activationTimestamp: Long = 0
    private var inactivityTimer: Job? = null
    private var vibrator: Vibrator? = null

    // 상수 정의
    companion object {
        const val DOUBLE_GESTURE_THRESHOLD_MS = 1500L // 1.5초 내 두 제스처 감지 시 비활성화
        const val INACTIVITY_TIMEOUT_MS = 30000L // 30초 동안 활동 없으면 자동 비활성화
    }

    // 콜백을 위한 속성
    var onGesture1Detected: (() -> Unit)? = null
    var onGesture2Detected: (() -> Unit)? = null
    var onGesture3Detected: (() -> Unit)? = null
    var onGesture4Detected: (() -> Unit)? = null

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
                var gestureId = -1
                if (text.startsWith("{")) {
                    val json = JSONObject(text)
                    gestureId = json.optInt("predicted", -1)
                    val gestureName = json.optString("gesture_name", "예측 없음")
                    _prediction.value = gestureName
                    _history.add(0, gestureName)
                } else {
                    try {
                        gestureId = text.toInt()
                        Log.d("WebSocket", "📊 제스처 감지: ID=$gestureId")

                        // 4, 5번 제스처는 표시하지 않음 => 가만히 있는자세.
                        // 2,3번 자세만 화면에 표시
                        if (gestureId != 4 && gestureId != 5) {
                            _prediction.value = text
                            _history.add(0, text)
                        } else {
                            _prediction.value = "-"
                            _history.add(0, "-")
                        }
//                        _prediction.value = text
//                        _history.add(0, text)
                    } catch (e: NumberFormatException) {
                        _prediction.value = text
                        _history.add(0, text)
                    }


                }

                // 특정 제스처 ID에 대한 처리
                when (gestureId) {
                    1 -> {
                        viewModelScope.launch { _gestureEvent.emit(1) }
                        processGesture1Detection()
                    }
                    2, 3 -> {
                        // 제스처 2, 3은 활성 모드에서만 처리
                        if (isGestureRecognitionActive()) {
                            viewModelScope.launch { _gestureEvent.emit(gestureId) }

                            // 제스처 2/3 감지 시 짧은 진동 한 번
                            provideGestureActionFeedback()

                            if (gestureId == 2) onGesture2Detected?.invoke()
                            else onGesture3Detected?.invoke()

                            notifyActivity()
                        }
                    }
//                    2 -> {
//                        if (isGestureRecognitionActive()) {
//                            viewModelScope.launch { _gestureEvent.emit(2) }
//                            onGesture2Detected?.invoke()
//                            notifyActivity() // 활동 감지하여 타이머 재설정
//                        }
//                    }
//                    3 -> {
//                        if (isGestureRecognitionActive()) {
//                            viewModelScope.launch { _gestureEvent.emit(3) }
//                            onGesture3Detected?.invoke()
//                            notifyActivity() // 활동 감지하여 타이머 재설정
//                        }
//                    }
                }
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
// 4. processGesture1Detection() 함수 수정 - 기존 함수 내용 변경
    // 제스처 1 감지 관련 변수들
    private var lastGesture1DetectionTime = 0L
    private val GESTURE1_DEBOUNCE_MS = 500L  // 디바운스 시간
    private val DOUBLE_GESTURE_THRESHOLD_MS = 1500L  // 두 번째 제스처 대기 시간
    private var awaitingSecondGesture = false
    private var secondGestureTimer: Job? = null

    /**
     * 제스처 1(손목 회전) 감지 처리 - 활성화/비활성화 토글
     * 개선된 버전: 연속 감지 방지 및 더 명확한 상태 관리
     */
    /**
     * 제스처 1(손목 회전) 감지 처리 - 항상 두 번의 연속 제스처 필요
     */
    fun processGesture1Detection() {
        val currentTime = System.currentTimeMillis()
        Log.d("GestureMode", "제스처 1 감지: 현재=${_recognitionMode.value}, 대기중=${awaitingSecondGesture}, 시간차=${currentTime - lastGesture1DetectionTime}ms")

        // 디바운싱: 연속 감지 방지
        if (currentTime - lastGesture1DetectionTime < GESTURE1_DEBOUNCE_MS) {
            Log.d("GestureMode", "제스처 1 감지: 디바운스 시간 내 무시됨")
            return
        }

        // 감지 시간 업데이트
        lastGesture1DetectionTime = currentTime

        if (awaitingSecondGesture) {
            // 두 번째 제스처 감지됨
            Log.d("GestureMode", "제스처 1 감지: 두 번째 제스처 확인!")

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
            provideModeChangeFeedback()

            // 대기 상태 해제
            awaitingSecondGesture = false

        } else {
            // 첫 번째 제스처 감지 - 두 번째 제스처 대기
            awaitingSecondGesture = true

            // 첫 번째 제스처는 햅틱 피드백 없음 (혼란 방지)

            // 두 번째 제스처 대기 타이머 시작
            startSecondGestureTimer()

            // 콜백 호출
            onGesture1Detected?.invoke()
        }
    }



    /** 두 번째 제스처 대기 타이머 시작*/
    private fun startSecondGestureTimer() {
        secondGestureTimer?.cancel()
        secondGestureTimer = viewModelScope.launch {
            delay(DOUBLE_GESTURE_THRESHOLD_MS)
            if (awaitingSecondGesture) {
                Log.d("GestureMode", "두 번째 제스처 대기 시간 초과: 대기 상태 해제")
                awaitingSecondGesture = false

                // 타임아웃 피드백 - 짧은 진동으로 안내
               // provideTimeoutFeedback()
            }
        }
    }

    /**
     * 첫 번째 제스처 감지 피드백 (짧은 진동)
     */
    private fun provideGestureDetectionFeedback() {
        vibrator?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // 아주 짧은 진동 (50ms)
                it.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(50)
            }
        }
    }

    /**
     * 타임아웃 피드백 (짧은 진동 2번)
     */
    private fun provideTimeoutFeedback() {
        vibrator?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // 짧은 진동 두 번 빠르게 (30ms + 50ms 간격 + 30ms)
                val timings = longArrayOf(0, 30, 50, 30)
                val amplitudes = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE)
                it.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(longArrayOf(0, 30, 50, 30), -1)
            }
        }
    }



    /**
     * 제스처 인식을 비활성화합니다.
     */
    // 5. 비활성화 함수 수정
    private fun deactivateGestureRecognition() {
        _recognitionMode.value = GestureRecognitionMode.INACTIVE

        // 센서 모드를 LOW_POWER로 설정
//        setSensorMode(SensorMode.LOW_POWER)

        inactivityTimer?.cancel()

        // UI 상태 업데이트
        _uiState.update { it.copy(
            isListening = false,
            showActivationIndicator = false,
            activationProgress = 0f
        ) }

        // 비활성화 피드백
//        provideDeactivationFeedback()
        provideModeChangeFeedback()

        Log.d("GestureMode", "제스처 인식 모드 비활성화 - 저전력 센서 모드로 전환")
    }
    /**
     * 제스처 인식 자동 타임아웃 타이머를 시작합니다.
     */
//    private fun startInactivityTimer() {
//        inactivityTimer?.cancel()
//        inactivityTimer = viewModelScope.launch {
//            delay(INACTIVITY_TIMEOUT_MS)
//
//            if (_recognitionMode.value == GestureRecognitionMode.ACTIVE ||
//                _recognitionMode.value == GestureRecognitionMode.ACTIVATING) {
//                deactivateGestureRecognition()
//                Log.d("GestureMode", "제스처 인식 모드 자동 비활성화 (타임아웃)")
//            }
//        }
//    }

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
            provideActivationFeedback()

            // 타이머 시작
            //startInactivityTimer()

            Log.d("GestureMode", "제스처 인식 모드 수동 활성화 (버튼) - 정상 센서 모드로 전환")
        }
    }
    /**
     * 활성화 햅틱 피드백을 제공합니다.
     */
    private fun provideActivationFeedback() {
        vibrator?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // 짧은 진동 한 번 (100ms)
                it.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(100)
            }
        }
    }

    /*** 모드 전환 피드백 (짧은 진동 두 번) */
    private fun provideModeChangeFeedback() {
        vibrator?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // 짧은 진동 두 번 빠르게 (30ms + 50ms 간격 + 30ms)
                val strongAmplitude = 255 // 최대 세기
                val timings = longArrayOf(0, 30, 50, 30)
                val amplitudes = intArrayOf(0, strongAmplitude, 0, strongAmplitude)
                it.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(longArrayOf(0, 30, 50, 30), -1)
            }
        }
    }

    /*** 제스처 2/3 감지 피드백 (짧은 진동 한 번)*/
    private fun provideGestureActionFeedback() {
        vibrator?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // 아주 짧은 진동 (강한 세기)
                val strongAmplitude = 255 // 최대 세기
                it.vibrate(VibrationEffect.createOneShot(50, strongAmplitude))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(50)
            }
        }
    }
}