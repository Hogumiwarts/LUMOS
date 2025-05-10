package com.hogumiwarts.lumos.routine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GestureResponse {

    @Schema(description = "제스처 ID", example = "1")
    private Long gestureId;

    @Schema(description = "제스처 이름", example = "핑거스냅")
    private String gestureName;

    @Schema(description = "제스처 이미지 URL", example = "https://example.com/img/gesture_snap.png")
    private String gestureImageUrl;

    @Schema(description = "제스처 설명", example = "손가락을 튕깁니다.")
    private String gestureDescription;
}
