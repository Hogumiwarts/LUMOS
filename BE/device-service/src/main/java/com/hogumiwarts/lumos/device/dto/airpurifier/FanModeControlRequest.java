package com.hogumiwarts.lumos.device.dto.airpurifier;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "공기청정기 FanMode 변경 DTO")
public class FanModeControlRequest {

    @Schema(
            description = "공기청정기 FanMode (Auto / Low / Medium / High / Quiet)",
            example = "Auto"
    )
    private FanMode fanMode;
}