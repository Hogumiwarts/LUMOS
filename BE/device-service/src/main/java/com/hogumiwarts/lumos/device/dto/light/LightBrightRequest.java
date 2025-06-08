package com.hogumiwarts.lumos.device.dto.light;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "디바이스 밝기 변경 요청 DTO")
public class LightBrightRequest {

    @Schema(description = "디바이스 밝기 요청", example = "0 ~ 100")
    private int brightness;
}
