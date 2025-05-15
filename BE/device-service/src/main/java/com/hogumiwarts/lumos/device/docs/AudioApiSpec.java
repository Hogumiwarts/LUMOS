package com.hogumiwarts.lumos.device.docs;

import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.dto.audio.AudioPlaybackResponse;
import com.hogumiwarts.lumos.device.dto.audio.AudioVolumnResponse;
import com.hogumiwarts.lumos.device.dto.audio.VolumeControlRequest;
import com.hogumiwarts.lumos.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface AudioApiSpec {

    @Operation(summary = "스피커 볼륨 Up/Down",
            description = "스피커의 볼륨을 0~100 사이의 값으로 설정합니다.",
            tags = {"스피커"})
    @PatchMapping("/{deviceId}/audio/volume")
    ResponseEntity<CommonResponse<AudioVolumnResponse>> updateAudioVolume(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원 ID와 전원 상태 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = VolumeControlRequest.class))
            )
            @RequestBody VolumeControlRequest request
    );

    @Operation(summary = "음악 재생/일시정지",
            description = """
                    음악을 재생하거나 일시정지(멈춤) 상태로 변경합니다.
                    - `true`: 재생 상태로 변경
                    
                    - `false`: 멈춤 상태로 변경
                    """,
            tags = {"스피커"})
    @PatchMapping("/{deviceId}/audio/playback")
    ResponseEntity<CommonResponse<AudioPlaybackResponse>>  updateAudioPlayback(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원 ID와 전원 상태 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PowerControlRequest.class))
            )
            @RequestBody PowerControlRequest request
    );

    @Operation(
            summary = "스피커 상태 조회",
            description = """
                스피커의 상태 정보를 조회합니다.

                📘 응답 필드 설명
                - `deviceImg` : 디바이스 이미지 URL

                - `deviceName` : 디바이스 이름 (SmartThings에 등록된 기기명)

                - `manufacturerCode` : 제조사 (예: Sonos)

                - `deviceModel` : 모델명 (예: Bookshelf)

                - `deviceType` : 디바이스 타입 (예: AUDIO)

                - `activated` : 전원 상태 (true = 켜짐, false = 꺼짐)

                - `audioImg` : 현재 재생 중인 음원의 앨범 이미지 URL

                - `audioName` : 현재 재생 중인 곡명

                - `audioArtist` : 현재 재생 중인 아티스트명

                - `audioVolume` : 현재 볼륨 (0~100)
                """,
            tags = {"스피커"}
    )
    @GetMapping("/{deviceId}/audio/status")
    ResponseEntity<?> getAudioStatus(
            @Parameter(description = "디바이스 ID", required = true)
            @PathVariable("deviceId") Long deviceId
    );

}