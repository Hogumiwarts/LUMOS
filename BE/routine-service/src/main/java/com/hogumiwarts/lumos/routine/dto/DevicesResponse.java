package com.hogumiwarts.lumos.routine.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DevicesResponse {
    @Schema(description = "디바이스 ID", example = "1")
    private Long deviceId;

    @Schema(description = "디바이스 이름", example = "내 방 조명")
    private String deviceName;

    @Schema(description = "디바이스 타입", example = "조명")
    private String deviceType;

    @Schema(description = "디바이스 이미지 URL", example = "https://example.com/img/lamp.png")
    private String deviceImageUrl;

    @Schema(description = "디바이스 제어 명령 목록")
    private List<CommandRequest> commands;
}