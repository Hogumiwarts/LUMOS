package com.hogumiwarts.lumos.routine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceDto {

    @Schema(description = "디바이스 ID", example = "1")
    private Long deviceId;

    @Schema(description = "디바이스 이름", example = "스마트 전등")
    private String deviceName;

    @Schema(description = "디바이스 이미지 URL", example = "https://cdn.example.com/img/light.png")
    private String deviceImg;

    @Schema(description = "디바이스 제어 명령 목록", example = "[{\"on\": true, \"brightness\": 80}]")
    private List<Map<String, Object>> control;
}
