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

public interface AudioApiSpec {

    @Operation(summary = "스피커 볼륨 Up/Down", description = "스피커의 볼륨을 Up, Down 조절 합니다.", tags = {"스피커"})
    @PatchMapping("/{deviceId}/audio/volume")
    ResponseEntity<CommonResponse<SuccessResponse>> updateAudioVolume(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원 ID와 전원 상태 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = VolumeControlRequest.class))
            )
            @RequestBody VolumeControlRequest request
    );

    @Operation(summary = "음악 재생/일시정지", description = "음악을 재생하거나 일시정지(멈춤) 상태로 변경합니다.", tags = {"스피커"})
    @PatchMapping("/{deviceId}/audio/playback")
    ResponseEntity<CommonResponse<SuccessResponse>>  updateAudioPlayback(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원 ID와 전원 상태 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PowerControlRequest.class))
            )
            @RequestBody PowerControlRequest request
    );

    @Operation(summary = "스피커 상태 조회", description = "스피커의 상태 정보를 조회 합니다.", tags = {"스피커"})
    @GetMapping("/{deviceId}/audio/status")
    ResponseEntity<?> getAudioStatus(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId
    );

}