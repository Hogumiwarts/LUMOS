package com.hogumiwarts.lumos.gesturesensor.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SensorDataRequest {

	@Schema(description = "제스처 ID (각 제스처 종류별 식별자)", example = "1")
	private Integer gestureId;

	@Schema(description = "워치 디바이스 고유 ID", example = "20158844c1f81690")
	private String watchDeviceId;

	@Schema(description = "시계열 센서 데이터 목록", requiredMode = Schema.RequiredMode.REQUIRED)
	private List<SensorValue> data;

	@Data
	public static class SensorValue {

		@Schema(description = "Unix 타임스탬프 (ms)", example = "1714297200000")
		private Long timestamp;

		@Schema(description = "X축 선형 가속도(중력 X)", example = "0.12")
		private Double accLiX;

		@Schema(description = "Y축 선형 가속도(중력 X)", example = "-0.03")
		private Double accLiY;

		@Schema(description = "Z축 선형 가속도(중력 X)", example = "9.81")
		private Double accLiZ;

		@Schema(description = "X축 가속도", example = "0.12")
		private Double accX;

		@Schema(description = "Y축 가속도", example = "-0.03")
		private Double accY;

		@Schema(description = "Z축 가속도", example = "9.81")
		private Double accZ;

		@Schema(description = "X축 자이로", example = "0.01")
		private Double gryoX;

		@Schema(description = "Y축 자이로", example = "-0.02")
		private Double gryoY;

		@Schema(description = "Z축 자이로", example = "0.00")
		private Double gryoZ;
	}
}
