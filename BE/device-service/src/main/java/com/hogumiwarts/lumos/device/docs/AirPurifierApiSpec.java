package com.hogumiwarts.lumos.device.docs;

import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface AirPurifierApiSpec {

    @Operation(summary = "공기청정기 전원 On/Off", description = "공기청정기의 전원을 제어 합니다.", tags = {"공기청정기"})
    @PatchMapping("/{deviceId}/airpurifier/power")
    ResponseEntity<CommonResponse<SuccessResponse>> updateAirPurifierPower(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원 ID와 전원 상태 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PowerControlRequest.class))
            )
            @RequestBody PowerControlRequest  request
    );

    @Operation(summary = "공기청정기 FanMode 변경", description = "공기청정기의 FanMode를 변경 합니다.", tags = {"공기청정기"})
    @PatchMapping("/{deviceId}/airpurifier/fanmode")
    ResponseEntity<CommonResponse<SuccessResponse>> updateAirPurifierFanMode(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId,
            @Parameter(
                    description = "FanMode 설정값 : auto, low, medium, high, quiet",
                    required = true,
                    content = @Content(schema = @Schema(implementation = FanModeControlRequest.class))
            )
            @RequestBody FanModeControlRequest request
    );

    @Operation(summary = "공기청정기 상태 조회", description = "공기청정기 필터 정보, 전원 상태 등을 확인합니다.", tags = {"공기청정기"})
    @GetMapping("/{deviceId}/airpurifier/status")
    ResponseEntity<CommonResponse<AirPurifierDetailResponse>> getAirPurifierStatus(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId
    );

}