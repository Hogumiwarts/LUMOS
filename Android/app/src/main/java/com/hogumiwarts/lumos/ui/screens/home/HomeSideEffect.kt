package com.hogumiwarts.lumos.ui.screens.home

/*
 * 일회성 UI 이벤트(토스트, 네비게이션 등)를 나타내는 SideEffect 클래스입니다.
 *
 * - MVI 패턴의 'SideEffect(부수 효과)'에 해당합니다.
 * - 화면에 직접적으로 영향을 주지만
 * - 상태(State)에 포함되면 안 되는 이벤트를 처리할 때 사용합니다.
 *
 * - 예: Toast 메시지, 다이얼로그, 화면 이동 등
 */

sealed class HomeSideEffect {
    data class ShowError(val message: String): HomeSideEffect()
}