package com.hogumiwarts.lumos.routine.dto;

import com.hogumiwarts.lumos.routine.entity.Routine;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "루틴 응답 DTO")
public class RoutineCreateResponse {

	@Schema(description = "루틴 ID", example = "1")
	private Long routineId;

	@Schema(description = "회원 ID", example = "10")
	private Long memberId;

	@Schema(description = "제스처 ID", example = "1")
	private Long gestureId;

	@Schema(description = "루틴 이름", example = "수면 루틴")
	private String routineName;

	@Schema(description = "루틴 아이콘", example = "lamp")
	private String routineIcon;

	@Schema(description = "기기 제어 정보")
	private List<DevicesCreateRequest> devices;

	public static RoutineCreateResponse from(Routine routine) {
		return RoutineCreateResponse.builder()
			.routineId(routine.getRoutineId())
			.memberId(routine.getMemberId())
			.gestureId(routine.getGestureId())
			.routineName(routine.getRoutineName())
			.routineIcon(routine.getRoutineIcon())
			.devices(routine.getDevices())
			.build();
	}
}
