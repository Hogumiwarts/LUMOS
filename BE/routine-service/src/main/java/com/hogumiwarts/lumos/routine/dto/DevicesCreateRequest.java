package com.hogumiwarts.lumos.routine.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevicesCreateRequest {

    @Schema(description = "디바이스 ID", example = "1")
    private Long deviceId;

    @Schema(description = "디바이스 제어 명령 목록")
    private List<CommandRequest> commands;
}
