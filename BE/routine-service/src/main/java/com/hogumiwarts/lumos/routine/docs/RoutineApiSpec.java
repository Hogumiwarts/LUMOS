package com.hogumiwarts.lumos.routine.docs;

import com.hogumiwarts.lumos.routine.dto.RoutineRequest;
import com.hogumiwarts.lumos.routine.dto.RoutineResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "루틴 정보 조회", description = "루틴 정보 조회 API입니다.")
public interface RoutineApiSpec {

	@Operation(summary = "루틴 정보 조회", description = """
        💡 지정된 memberId와 routineId로 루틴 정보를 조회합니다.
        """)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "요청 성공"),
			@ApiResponse(responseCode = "400", description = "유효하지 않은 요청입니다."),
			@ApiResponse(responseCode = "500", description = "서버 오류 발생")
	})
	ResponseEntity<RoutineResponse> getRoutineDevices(
			@RequestParam Long memberId,
			@PathVariable Long routineId
	);
}
