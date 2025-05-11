package com.hogumiwarts.lumos.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineResponse {

	private Long routineId;
	private String routineName;
}

