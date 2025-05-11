package com.hogumiwarts.lumos.gesturesensor.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SensorDataRequest {

	@JsonAlias({"gestureId", "gesture_id"})
	@Schema(description = "제스처 ID (각 제스처 종류별 식별자)", example = "1")
	private Integer gestureId;

	@JsonAlias({"watchDeviceId", "watch_device_id"})
	@Schema(description = "워치 디바이스 고유 ID", example = "20158844c1f81690")
	private String watchDeviceId;

	@Schema(description = "시계열 센서 데이터 목록", requiredMode = Schema.RequiredMode.REQUIRED)
	private List<SensorValue> data;

	@Data
	public static class SensorValue {

		@Schema(description = "Unix 타임스탬프 (ms)", example = "1714297200000")
		private Long timestamp;

		@JsonAlias({"liAccX", "li_acc_x"})
		@Schema(description = "X축 선형 가속도(중력 X)", example = "0.12")
		private Double liAccX;

		@JsonAlias({"liAccY", "li_acc_y"})
		@Schema(description = "Y축 선형 가속도(중력 X)", example = "-0.03")
		private Double liAccY;

		@JsonAlias({"liAccZ", "li_acc_z"})
		@Schema(description = "Z축 선형 가속도(중력 X)", example = "9.81")
		private Double liAccZ;

		@JsonAlias({"accX", "acc_x"})
		@Schema(description = "X축 가속도", example = "0.12")
		private Double accX;

		@JsonAlias({"accY", "acc_y"})
		@Schema(description = "Y축 가속도", example = "-0.03")
		private Double accY;

		@JsonAlias({"accZ", "acc_z"})
		@Schema(description = "Z축 가속도", example = "9.81")
		private Double accZ;

		@JsonAlias({"gryoX", "gryo_x"})
		@Schema(description = "X축 자이로", example = "0.01")
		private Double gryoX;

		@JsonAlias({"gryoY", "gryo_y"})
		@Schema(description = "Y축 자이로", example = "-0.02")
		private Double gryoY;

		@JsonAlias({"gryoZ", "gryo_z"})
		@Schema(description = "Z축 자이로", example = "0.00")
		private Double gryoZ;
	}
}
