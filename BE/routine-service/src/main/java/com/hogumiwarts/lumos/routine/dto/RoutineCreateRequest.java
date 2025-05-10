package com.hogumiwarts.lumos.routine.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class RoutineCreateRequest {

	@Schema(description = "루틴 이름", example = "수면중")
	private String routineName;

	@Schema(description = "루틴 아이콘", example = "")
	private Integer routineIcon;

	@Schema(description = "루틴에 추가할 기기 정보", example = "")
	private List<DeviceRequest> devices;

	@Schema(description = "제스처 id", example = "1")
	private Long gestureId;
}
