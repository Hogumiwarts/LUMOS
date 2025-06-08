package com.hogumiwarts.lumos.routine.dto;

import java.util.List;

import com.hogumiwarts.lumos.routine.entity.Routine;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "루틴 응답 DTO")
public class RoutineDetailResponse {

	@Schema(description = "루틴 이름", example = "수면 루틴")
	private String routineName;

	@Schema(description = "루틴 아이콘", example = "lamp")
	private String routineIcon;

	@Schema(description = "기기 정보")
	private List<DevicesCreateResponse> devices;

	@Schema(description = "제스처 ID", example = "1")
	private Long gestureId;

	@Schema(description = "제스처 이름", example = "핑거스냅")
	private String gestureName;

	@Schema(description = "제스처 이미지 URL", example = "https://example.com/img/gesture_snap.png")
	private String gestureImageUrl;

	@Schema(description = "제스처 설명", example = "손가락을 튕깁니다.")
	private String gestureDescription;

	public static RoutineDetailResponse from(Routine routine, GestureResponse gestureResponse, List<DevicesCreateResponse> devices) {
		return RoutineDetailResponse.builder()
			.routineName(routine.getRoutineName())
			.routineIcon(routine.getRoutineIcon())
			.devices(devices)
			.gestureId(routine.getGestureId())
			.gestureName(gestureResponse != null ? gestureResponse.getGestureName() : null)
			.gestureImageUrl(gestureResponse != null ? gestureResponse.getGestureImageUrl() : null)
			.gestureDescription(gestureResponse != null ? gestureResponse.getGestureDescription() : null)
			.build();
	}
}
