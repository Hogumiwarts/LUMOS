package com.hogumiwarts.lumos.device.dto.light;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LightColorResponse {

    @Schema(description = "값 변경 성공 여부", example = "true: 값 변경 성공 / false: 값 변경 실패")
    private Boolean success;

    @Schema(description = "색조(Hue) 값 (0~100)", example = "75")
    private Integer hue;

    @Schema(description = "채도(Saturation) 값 (0.0~100.0)", example = "88.5")
    private Float saturation;
}
