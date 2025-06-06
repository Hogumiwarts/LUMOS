package com.hogumiwarts.lumos.device.dto.minibig;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SwitchStatusResponse {

    @Schema(description = "값 변경 성공 여부", example = "true: 값 변경 성공 / false: 값 변경 실패")
    private Boolean success;

    @Schema(description = "디바이스 제어 상태 (true: ON, false: OFF)", example = "false")
    private boolean activated;
}
