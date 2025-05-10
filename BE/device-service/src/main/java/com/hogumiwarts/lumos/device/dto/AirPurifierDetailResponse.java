package com.hogumiwarts.lumos.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "스위치 디바이스 상태 상세 응답 DTO")
public class AirPurifierDetailResponse {

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

    @Schema(description = "현재 스위치 전원 상태", example = "true")
    private Boolean activated;

    @Schema(description = "공기질 CAQI 등급 (VeryLow, Low, Medium, High, VeryHigh, UNKNOWN)")
    private String caqi;

    @Schema(description = "현재 냄새 센서 수치")
    private Integer odorLevel;

    @Schema(description = "미세먼지 농도 (단위: ㎍/m³)")
    private Integer dustLevel;

    @Schema(description = "초미세먼지 농도 (단위: ㎍/m³)")
    private Integer fineDustLevel;

    @Schema(description = "팬 속도 (예: auto, low, medium, high, quiet)")
    private String fanMode;

    @Schema(description = "필터 누적 사용 시간 (단위: 시간)")
    private Integer filterUsageTime;
}