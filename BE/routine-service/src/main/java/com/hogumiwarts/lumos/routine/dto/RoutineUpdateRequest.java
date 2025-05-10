package com.hogumiwarts.lumos.routine.dto;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
public class RoutineUpdateRequest {
    private Optional<Long> gestureId = Optional.empty();
    private Optional<String> routineName = Optional.empty();
    private Optional<Integer> routineIcon = Optional.empty();
    private Optional<List<Map<String, Object>>> devices = Optional.empty();
}