package com.hogumiwarts.lumos.routine.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RoutineRequest {

	@Schema(description = "멤버 ID", example = "1")
	private Long memberId;

}
