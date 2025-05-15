package com.hogumiwarts.lumos.device.dto.light;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "디바이스 색상 변경 요청 DTO입니다. hue(색조)는 0~100의 정수, saturation(채도)는 0~100의 실수입니다.")
public class LightColorRequest {

    @Schema(description = "색조(Hue) 값 (0~100)", example = "75")
    @Min(0)
    @Max(100)
    private Integer hue;

    @Schema(description = "채도(Saturation) 값 (0.0~100.0)", example = "88.5")
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Float saturation;
}