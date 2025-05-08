package com.hogumiwarts.lumos.routine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GestureInfo {

    @Schema(description = "제스처 ID", example = "1")
    private Long memberGestureId;

    @Schema(description = "제스처 이름", example = "핑거스냅")
    private String gestureName;

    @Schema(description = "제스처 이미지 URL", example = "https://cdn.example.com/img/gesture_snap.png")
    private String gestureImg;
}
