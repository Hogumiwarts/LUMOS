package com.hogumiwarts.lumos.routine.dto;

import lombok.Builder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "루틴 목록 응답 DTO")
public class RoutineListResponse {

    @Schema(description = "루틴 ID", example = "1")
    private Long routineId;

    @Schema(description = "루틴 이름", example = "수면 루틴")
    private String routineName;

    @Schema(description = "루틴 아이콘", example = "lamp")
    private String routineIcon;

    @Schema(description = "제스처 이름", example = "수면 루틴")
    private String gestureName;
}
