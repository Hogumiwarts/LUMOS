package com.hogumiwarts.lumos.device.dto.light;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LightBrightnessResponse {

    @Schema(description = "값 변경 성공 여부", example = "true: 값 변경 성공 / false: 값 변경 실패")
    private Boolean success;

    @Schema(description = "디바이스 밝기 요청", example = "0 ~ 100")
    private Integer brightness;
}
