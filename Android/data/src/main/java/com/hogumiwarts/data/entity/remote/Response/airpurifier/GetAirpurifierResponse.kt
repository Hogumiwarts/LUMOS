package com.hogumiwarts.data.entity.remote.Response.airpurifier

data class GetAirpurifierResponse(
    val tagNumber: Int,
    val deviceId: Long,                 // SmartThigns 에 저장된 기기명
    val deviceImg: String,
    val deviceName: String,
    val manufacturerCode: String,       // 제조사
    val deviceModel: String,            // 모델명
    val deviceType: String,             // 기기 타입
    val activated: Boolean,             // 전원 상태 (true = 켜짐, false = 꺼짐)
    val caqi: String,                   // 공기질 등급 (VeryLow / Low / Medium / High / VeryHigh / UNKNOWN)
    val odorLevel: Int,
    val dustLevel: Int,                 // 미세먼지(㎍/m³) / fineDustLevel : 초미세먼지(㎍/m³) / odorLevel : 현재 냄새 센서 수치
    val fineDustLevel: Int,
    val fanMode: String,                // 팬 속도 (auto / low / medium / high / quiet)
    val filterUsageTime: Int,           // 필터 누적 사용 시간 (단위: 시간)
)
