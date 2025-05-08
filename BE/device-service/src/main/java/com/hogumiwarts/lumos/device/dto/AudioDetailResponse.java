package com.hogumiwarts.lumos.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "스위치 디바이스 상태 상세 응답 DTO")
public class AudioDetailResponse {

    @Schema(description = "디바이스 태그 번호")
    private Integer tagNumber;

    @Schema(description = "디바이스 ID")
    private Long deviceId;

    @Schema(description = "디바이스 이미지 URL")
    private String deviceImg;

    @Schema(description = "디바이스 이름")
    private String deviceName;

    @Schema(description = "디바이스 제조사")
    private String manufacturerCode;

    @Schema(description = "디바이스 모델명")
    private String deviceModel;

    @Schema(description = "디바이스 타입")
    private String deviceType;

    @Schema(description = "현재 오디오 전원 상태", example = "true")
    private Boolean activated;

    @Schema(description = "재생중인 앨범 이미지")
    private String audioImg;

    @Schema(description = "노래 제목")
    private String audioName;

    @Schema(description = "가수")
    private String audioArtist;

    @Schema(description = "오디오 볼륨(1~100)")
    private Integer audioVolume;
}