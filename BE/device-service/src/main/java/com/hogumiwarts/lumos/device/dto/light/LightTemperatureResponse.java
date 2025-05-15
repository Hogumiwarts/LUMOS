package com.hogumiwarts.lumos.device.dto.light;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LightTemperatureResponse {

    @Schema(description = "값 변경 성공 여부", example = "true: 값 변경 성공 / false: 값 변경 실패")
    private Boolean success;

    @Schema(description = "디바이스 색 온도 변경 요청", example = "2200K ~ 6500K")
    private Integer temperature;
}
