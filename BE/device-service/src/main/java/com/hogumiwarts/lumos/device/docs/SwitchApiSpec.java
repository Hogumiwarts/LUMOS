package com.hogumiwarts.lumos.device.docs;

import com.hogumiwarts.lumos.device.dto.DeviceResponse;
import com.hogumiwarts.lumos.device.dto.DeviceStatusResponse;
import com.hogumiwarts.lumos.device.dto.PowerControlRequest;
import com.hogumiwarts.lumos.device.dto.VolumeControlRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface SwitchApiSpec {

    @Operation(summary = "스위치 전원 On/Off", description = "스위치의 전원을 제어 합니다.", tags = {"스위치"})
    @PatchMapping("/{deviceId}/switch/power")
    ResponseEntity<?> updateSwitchPower(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원 ID와 전원 상태 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PowerControlRequest.class))
            )
            @RequestBody PowerControlRequest request
    );

    @Operation(summary = "스위치 상태 조회", description = "조명의 밝기, 전원 상태 등을 확인합니다.", tags = {"스위치"})
    @GetMapping("/{deviceId}/switch/status")
    ResponseEntity<?> getSwitchStatus(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId,
            @Parameter(description = "회원 ID", required = true)
            @RequestParam("memberId") Long memberId
    );


}