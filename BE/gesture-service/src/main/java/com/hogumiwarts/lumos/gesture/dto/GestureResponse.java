package com.hogumiwarts.lumos.gesture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GestureResponse {

    @Schema(description = "제스처 ID", example = "1")
    private Long memberGestureId;

    @Schema(description = "제스처 이름", example = "핑거스냅")
    private String gestureName;

    @Schema(description = "제스처 대표 이미지 URL", example = "http://example.com/image1.jpg")
    private String gestureImg;
}
