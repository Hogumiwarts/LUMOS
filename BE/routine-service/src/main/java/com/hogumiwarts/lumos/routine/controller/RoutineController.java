package com.hogumiwarts.lumos.routine.controller;

import com.hogumiwarts.lumos.routine.docs.RoutineApiSpec;
import com.hogumiwarts.lumos.routine.dto.RoutineResponse;
import com.hogumiwarts.lumos.routine.service.RoutineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/routine")
@RequiredArgsConstructor
public class RoutineController implements RoutineApiSpec {

	private final RoutineService routineService;

	@GetMapping("/{routineId}/devices")
	public ResponseEntity<RoutineResponse> getRoutineDevices(
			@RequestParam Long memberId,
			@PathVariable Long routineId
	) {
		RoutineResponse response = routineService.getRoutines(routineId);
		return ResponseEntity.ok(response);
	}
}
