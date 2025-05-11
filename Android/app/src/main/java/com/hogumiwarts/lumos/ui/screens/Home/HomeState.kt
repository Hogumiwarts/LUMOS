package com.hogumiwarts.lumos.ui.screens.Home

import com.hogumiwarts.domain.model.WeatherInfo

/*
 * Home 화면이 관찰할 단일 상태(State)를 정의하는 데이터 클래스입니다.
 *
 * - MVI 패턴의 'Model(State)'에 해당합니다.
 * - View는 이 상태를 구독하여 화면을 렌더링합니다.
 * - 상태는 불변성을 유지하며, ViewModel에서 매번 복사(copy)하여 갱신합니다.
 *
 * 예: 로딩 여부, 현재 날씨 데이터, 에러 메시지 등
 */

data class HomeState(
    val isLoading: Boolean = false,
    val weatherInfo: WeatherInfo? = null,
    val errorMessage: String? = null
)
