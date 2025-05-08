package com.hogumiwarts.lumos.routine.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoutineResponse {
    private Long routineId;
    private String routineName;
    private Integer routineIcon;
    private String gestureName;
}