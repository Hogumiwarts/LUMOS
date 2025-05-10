package com.hogumiwarts.lumos.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "디바이스 전원 제어 요청 DTO")
public class PowerControlRequest {
    @Schema(description = "디바이스 제어 상태 (true: ON, false: OFF)", example = "false")
    private Boolean activated;
}