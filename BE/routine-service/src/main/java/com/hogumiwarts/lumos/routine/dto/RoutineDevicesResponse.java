package com.hogumiwarts.lumos.routine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineDevicesResponse {

    @Schema(description = "제스처 이름", example = "핑거스냅")
    private String gestureName;

    @Schema(description = "제스처 이미지 URL", example = "https://cdn.example.com/img/gesture_snap.png")
    private String gestureImg;

    @Schema(description = "루틴에 포함된 디바이스 목록")
    private List<DeviceDto> devices;
}
