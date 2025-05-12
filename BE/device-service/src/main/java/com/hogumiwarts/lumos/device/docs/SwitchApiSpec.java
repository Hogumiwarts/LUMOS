package com.hogumiwarts.lumos.device.docs;

import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.dto.CommonResponse;
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

    @Operation(summary = "스위치 전원 On/Off",
            description = """
                    스위치의 전원을 제어 합니다. 
                    - `true`: 전원 켜기  / `false`: 전원 끄기
                    
                    """,
            tags = {"스위치"})
    @PatchMapping("/{deviceId}/switch/power")
    ResponseEntity<CommonResponse<SuccessResponse>> updateSwitchPower(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원 ID와 전원 상태 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PowerControlRequest.class))
            )
            @RequestBody PowerControlRequest  request
    );

    @Operation(summary = "스위치 상태 조회",
            description = """
                    스위치의 상태를 조회합니다.
                    
                    📘 응답 필드 설명
                    - `deviceName` : SmartThigns 에 저장된 기기명
                    
                    - `manufacturerCode` : 제조사
                    
                    - `deviceModel` : 모델명
                    
                    - `deviceType` : 기기 타입
                    
                    - `activated` : 전원 상태 (true = 켜짐, false = 꺼짐)
                    """,
            tags = {"스위치"})
    @GetMapping("/{deviceId}/switch/status")
    ResponseEntity<?> getSwitchStatus(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId
    );

}