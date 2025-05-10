package com.hogumiwarts.lumos.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "디바이스 색상 변경 요청 DTO")
public class LightColorRequest {

    @Schema(description = "디바이스 색상 변경 요청", example = "#FF0000")
    private String lightColor;

}