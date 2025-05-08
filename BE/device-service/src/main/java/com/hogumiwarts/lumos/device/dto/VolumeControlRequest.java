package com.hogumiwarts.lumos.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "디바이스 볼륨 제어 요청 DTO")
public class VolumeControlRequest {
    @Schema(description = "스피커 볼륨 설정", example = "35")
    private int volume;
}