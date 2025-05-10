package com.hogumiwarts.lumos.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "디바이스 색 온도 요청 DTO")
public class LightTemperatureRequest {

    @Schema(description = "디바이스 색 온도 변경 요청", example = "2200K ~ 6500K")
    private int temperature;
}
