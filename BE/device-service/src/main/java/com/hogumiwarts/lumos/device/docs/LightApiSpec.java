package com.hogumiwarts.lumos.device.docs;

import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface LightApiSpec {

    @Operation(summary = "조명 전원 On/Off", description = "조명의 전원을 제어 합니다.", tags = {"조명"})
    @PatchMapping("/{deviceId}/light/power")
    ResponseEntity<CommonResponse<SuccessResponse>> updateLightStatus(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId,
            @Parameter(
                    description = "조명 전원 상태 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PowerControlRequest.class))
            )
            @RequestBody PowerControlRequest request
    );

    @Operation(summary = "조명 색상 변경", description = "조명의 색상을 변경합니다.", tags = {"조명"})
    @PatchMapping("/{deviceId}/light/color")
    ResponseEntity<CommonResponse<SuccessResponse>> updateLightColor(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId,
            @Parameter(
                    description = "조명 색상 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LightColorRequest.class))
            )
            @RequestBody LightColorRequest request
    );

    @Operation(summary = "조명 색 온도 변경", description = "조명의 색 온도를 변경합니다.", tags = {"조명"})
    @PatchMapping("/{deviceId}/light/temperature")
    ResponseEntity<CommonResponse<SuccessResponse>> updateLightTemperature(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId,
            @Parameter(
                    description = "조명 색상 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LightTemperatureRequest.class))
            )
            @RequestBody LightTemperatureRequest request
    );


    @Operation(summary = "조명 밝기 조절", description = "조명의 밝기를 변경합니다.", tags = {"조명"})
    @PatchMapping("/{deviceId}/light/bright")
    ResponseEntity<CommonResponse<SuccessResponse>> updateLightBright(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId,
            @Parameter(
                    description = "조명 밝기 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LightBrightRequest.class))
            )
            @RequestBody LightBrightRequest request
    );

    @Operation(summary = "조명 상태 조회", description = "조명의 밝기, 전원 상태 등을 확인합니다.", tags = {"조명"})
    @GetMapping("/{deviceId}/light/status")
    ResponseEntity<CommonResponse<LightDetailResponse>> getLightStatus(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId
    );


}
