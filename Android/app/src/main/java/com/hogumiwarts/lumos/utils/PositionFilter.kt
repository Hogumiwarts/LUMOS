//package com.hogumiwarts.lumos.utils
//
//import android.util.Log
//import androidx.core.uwb.RangingMeasurement
//import androidx.core.uwb.RangingPosition
//import kotlin.math.abs
//
///**
// * UWB 레인징 결과에 로우패스 필터를 적용하는 클래스
// * 방위각, 고도, 거리 데이터의 노이즈를 제거하기 위해 사용
// */
//class PositionFilter(
//    private val alphaDistance: Float = 0.1f, // 거리에 대한 필터 강도
//    private val alphaAngle: Float = 0.05f    // 각도(방위각, 고도)에 대한 필터 강도
//) {
//    // 필터링된 값 저장
//    private var filteredDistance: Float? = null
//    private var filteredAzimuth: Float? = null
//    private var filteredElevation: Float? = null
//
//    /**
//     * 방위각과 고도에 대한 로우패스 필터 적용
//     * 각도의 경우 -90도에서 90도 사이의 값에 특화된 처리 필요
//     */
//    private fun filterAngle(newValue: Float, previousValue: Float?): Float {
//        if (previousValue == null) {
//            return newValue
//        }
//
//        // 각도 차이 계산 (최소 경로로)
//        var diff = newValue - previousValue
//
//        // -90도에서 90도 사이의 값을 다루기 위한 특별 처리
//        if (diff > 90) {
//            diff -= 180
//        } else if (diff < -90) {
//            diff += 180
//        }
//
//        // 필터링된 값 계산
//        val filtered = previousValue + alphaAngle * diff
//
//        // 범위 내 값으로 유지
//        return when {
//            filtered > 90 -> filtered - 180
//            filtered < -90 -> filtered + 180
//            else -> filtered
//        }
//    }
//
//    /**
//     * 거리에 대한 로우패스 필터 적용
//     */
//    private fun filterDistance(newValue: Float, previousValue: Float?): Float {
//        if (previousValue == null) {
//            return newValue
//        }
//
//        // 일반적인 로우패스 필터 공식 적용
//        return alphaDistance * newValue + (1 - alphaDistance) * previousValue
//    }
//
//    /**
//     * 레인징 위치에 필터 적용
//     */
//    fun filter(position: RangingPosition): RangingPosition {
//        // 거리 필터링
//        val newDistance = position.distance?.value?.let { value ->
//            filteredDistance = filterDistance(value, filteredDistance)
//            RangingMeasurement(filteredDistance!!)
//        } ?: position.distance
//
//        // 방위각 필터링
//        val newAzimuth = position.azimuth?.value?.let { value ->
//            filteredAzimuth = filterAngle(value, filteredAzimuth)
//            RangingMeasurement(filteredAzimuth!!)
//        } ?: position.azimuth
//
//        // 고도 필터링
//        val newElevation = position.elevation?.value?.let { value ->
//            filteredElevation = filterAngle(value, filteredElevation)
//            RangingMeasurement(filteredElevation!!)
//        } ?: position.elevation
//
//        // 필터링된 위치 객체 반환
//        return RangingPosition(
//            newDistance,
//            newAzimuth,
//            newElevation,
//            position.elapsedRealtimeNanos
//        )
//    }
//
//    /**
//     * 필터 초기화
//     */
//    fun reset() {
//        filteredDistance = null
//        filteredAzimuth = null
//        filteredElevation = null
//    }
//}