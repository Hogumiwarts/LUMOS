package com.hogumiwarts.domain.model.audio

data class AudioStatusData(
    val tagNumber: Int,
    val deviceId: Long,                 // SmartThigns 에 저장된 기기명
    val deviceImg: String?,
    val deviceName: String,
    val manufacturerCode: String,       // 제조사
    val deviceModel: String,            // 모델명
    val deviceType: String,             // 기기 타입
    val activated: Boolean,             // 전원 상태 (true = 켜짐, false = 꺼짐)
    val audioImg: String,               // 현재 재생 중인 음원의 앨범 이미지 URL
    val audioName: String,              // 현재 재생 중인 곡명
    val audioArtist: String,            // 현재 재생 중인 아티스트명
    val audioVolume: Int,               // 현재 볼륨 (0~100)
)
