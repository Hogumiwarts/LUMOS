package com.hogumiwarts.lumos.ui.screens.Gesture

import com.hogumiwarts.domain.model.GestureResult

sealed class GestureState { // 클래스 계층의 하위 클래스들을 컴파일 타임에 모두 알 수 있도록 제한하는 특별한 클래스
    object Idle: GestureState()
    object Loading: GestureState()
    data class LoadedGesture(val data: GestureResult): GestureState()
}