package com.hogumiwarts.lumos.util

object GestureConstants {
    // 시간 관련 상수
    const val DOUBLE_GESTURE_THRESHOLD_MS = 1500L  // 1.5초 내 두 제스처 감지 시 비활성화
    const val INACTIVITY_TIMEOUT_MS = 30000L       // 30초 동안 활동 없으면 자동 비활성화
    const val GESTURE1_DEBOUNCE_MS = 1000L          // 제스처 1 감지 디바운스 시간
    const val GESTURE_PAUSE_DURATION_MS = 1500L    // 제스처 감지 후 일시 중지 시간

    // 진동 관련 상수
    const val VIBRATION_AMPLITUDE_STRONG = 255     // 강한 진동 세기
    const val VIBRATION_DURATION_SHORT = 50L       // 짧은 진동 지속 시간 (ms)
    const val VIBRATION_DURATION_MEDIUM = 100L     // 중간 진동 지속 시간 (ms)

    // 센서 관련 상수
    const val SLIDING_WINDOW_SIZE = 50             // 센서 데이터 윈도우 크기
    const val SLIDING_STEP = 5                     // 슬라이딩 윈도우 스텝 크기
}
