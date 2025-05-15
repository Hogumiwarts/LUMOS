package com.hogumiwarts.lumos.device.dto.light;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "조명 디바이스 상태 상세 응답 DTO")
public class LightDetailResponse {

    @Schema(description = "디바이스 태그 번호")
    private Integer tagNumber;

    @Schema(description = "디바이스 ID")
    private Long deviceId;

    @Schema(description = "디바이스 이미지 URL")
    private String deviceImg;

    @Schema(description = "디바이스 이름")
    private String deviceName;

    @Schema(description = "디바이스 제조사", example = "WiZ Connected")
    private String manufacturerCode;

    @Schema(description = "디바이스 모델명")
    private String deviceModel;

    @Schema(description = "디바이스 타입", example = "컬러 조명")
    private String deviceType;

    @Schema(description = "현재 조명 전원 상태", example = "true")
    private Boolean activated;

    @Schema(description = "조명 밝기")
    private Integer brightness;

    @Schema(description = "조명 색 온도")
    private Integer lightTemperature;

    @Schema(description = "조명 색조")
    private Integer hue;

    @Schema(description = "조명 채도")
    private Float saturation;

}