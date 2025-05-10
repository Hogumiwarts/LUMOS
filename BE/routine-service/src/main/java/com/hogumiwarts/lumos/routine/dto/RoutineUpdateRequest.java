package com.hogumiwarts.lumos.routine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class RoutineUpdateRequest {

    @Schema(description = "루틴 이름", example = "수면중")
    private Optional<String> routineName;

    @Schema(description = "루틴 아이콘", example = "취침")
    private Optional<String> routineIcon;

    @Schema(description = "루틴에 추가할 기기 정보", example = "")
    private Optional<List<DevicesCreateRequest>> devices;

    @Schema(description = "제스처 id", example = "1")
    private Optional<Long> gestureId;
}