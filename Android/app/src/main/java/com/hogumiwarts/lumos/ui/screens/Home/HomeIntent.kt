package com.hogumiwarts.lumos.ui.screens.Home

/*
 * Home 화면에서 발생할 수 있는 사용자 액션이나 시스템 이벤트를 정의하는 Intent 클래스입니다.
 *
 * - MVI 패턴의 'Intent'에 해당하며, 사용자의 상호작용을 표현합니다.
 * - ViewModel은 이 Intent를 받아서 상태(State)를 갱신하거나 SideEffect를 발생시킵니다.
 *
 * 예: 화면 진입 시 데이터 로드, 사용자 리프레시 요청 등
 */

sealed class HomeIntent {
//    object LoadWeather   : HomeIntent()    // 화면 진입·리프레시 시

    // 화면 진입 혹은 새로고침 시 위도·경도 정보를 함께 넘겨서 날씨를 로드
    data class LoadWeather(val latitude: Double, val longitude: Double) : HomeIntent()

}