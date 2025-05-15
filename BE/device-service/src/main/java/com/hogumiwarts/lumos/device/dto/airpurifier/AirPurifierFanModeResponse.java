package com.hogumiwarts.lumos.device.dto.airpurifier;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirPurifierFanModeResponse {

    @Schema(description = "값 변경 성공 여부", example = "true: 값 변경 성공 / false: 값 변경 실패")
    private Boolean success;

    @Schema(description = "디바이스 팬 모드 제어 상태", example = "auto")
    private String fanMode;

}
