package com.hogumiwarts.lumos.gesturesensor.dto;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "제스처 분류 결과 응답 모델")
public class PredictionResult {

	@Schema(description = "요청으로 전달된 실제 제스처 ID (정답)", example = "1")
	private int ground_truth;

	@Schema(description = "모델이 예측한 제스처 ID", example = "1")
	private int predicted;

	@Schema(description = "예측 결과가 정답과 일치하는지 여부", example = "true")
	private boolean match;
}
