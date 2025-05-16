package com.hogumiwarts.lumos.device.docs;

import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.dto.airpurifier.AirPurifierDetailResponse;
import com.hogumiwarts.lumos.device.dto.airpurifier.AirPurifierFanModeResponse;
import com.hogumiwarts.lumos.device.dto.airpurifier.AirPurifierStatusResponse;
import com.hogumiwarts.lumos.device.dto.airpurifier.FanModeControlRequest;
import com.hogumiwarts.lumos.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface AirPurifierApiSpec {

    @Operation(
            summary = "공기청정기 전원 On/Off",
            description = """
                    공기청정기의 전원을 제어합니다. 
                    - `true`: 전원 켜기  / `false`: 전원 끄기
                    
                    """,
            tags = {"공기청정기"}
    )
    @PatchMapping("/{deviceId}/airpurifier/power")
    ResponseEntity<CommonResponse<AirPurifierStatusResponse>> updateAirPurifierPower(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = PowerControlRequest.class))
            )
            @RequestBody PowerControlRequest request
    );

    @Operation(
            summary = "공기청정기 FanMode 변경",
            description = """
                    FanMode 설정값은 다음 중 하나입니다 (5개): 
                    - `Auto`: 자동 / `Low`: 약풍 / `Medium`: 중간풍 / `High`: 강풍 / `Quiet`: 조용한 모드  
                    """,
            tags = {"공기청정기"}
    )
    @PatchMapping("/{deviceId}/airpurifier/fanmode")
    ResponseEntity<CommonResponse<AirPurifierFanModeResponse>> updateAirPurifierFanMode(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId,
            @RequestBody FanModeControlRequest request
    );

    @Operation(
            summary = "공기청정기 상태 조회",
            description = """
                    공기청정기 상태(전원, 팬 모드, 공기질 등)를 조회합니다.
                    
                    📘 응답 필드 설명
                    - `deviceName` : SmartThigns 에 저장된 기기명
                    
                    - `manufacturerCode` : 제조사
                    
                    - `deviceModel` : 모델명
                    
                    - `deviceType` : 기기 타입
                    
                    - `activated` : 전원 상태 (true = 켜짐, false = 꺼짐)  
                    
                    - `fanMode` : 팬 속도 (Auto / Low / Medium / High / Quiet)  
                    
                    - `caqi` : 공기질 등급 (VeryLow / Low / Medium / High / VeryHigh / UNKNOWN)  
                    
                    - `filterUsageTime` : 필터 누적 사용 시간 (단위: 시간)  
                    
                    - `dustLevel : 미세먼지(㎍/m³)` / `fineDustLevel : 초미세먼지(㎍/m³)` / `odorLevel : 현재 냄새 센서 수치`
                    """,
            tags = {"공기청정기"}
    )
    @GetMapping("/{deviceId}/airpurifier/status")
    ResponseEntity<CommonResponse<AirPurifierDetailResponse>> getAirPurifierStatus(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId
    );

}